package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.IMessage
import com.catinthedark.math.Vector2
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.models.*
import com.catinthedark.squatality.server.math.IntersectService
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This class supposed to work in single thread.
 * So we do not need synchronized and concurrent collections any more.
 */
class RoomService(
    private val executor: IExecutor,
    private val maxPlayers: Int
) {
    private val logger = LoggerFactory.getLogger(RoomService::class.java)!!
    private val players: MutableMap<UUID, Player> = hashMapOf()
    private val bonuses: MutableList<BonusModel> = arrayListOf()
    private val bricks: MutableList<Brick> = arrayListOf()
    private var time: Long = 0
    private val intersect = IntersectService()
    val onlinePlayers: Map<UUID, Player>
        get() = players.filter { it.value.isOnline }
    var playing: Boolean = true
        private set(value) {
            field = value
        }
    /**
     * In this queue we can put messages to send them out of system.
     * For example, send some game events, that should not be in the game state.
     * I.e. kill-events,...
     */
    val output: Queue<Message> = LinkedList()

    fun playersExcept(id: UUID): Set<UUID> {
        return onlinePlayers.filterKeys { it != id }.keys
    }

    fun onNewClient(msg: HelloMessage, clientID: UUID): UUID? {
        logger.info("onNewClient: $msg; playersInRoom: ${onlinePlayers.size}")
        if (hasFreePlace()) {
            val pos = Const.Balance.randomSpawn()
            val player = Player(PlayerModel(
                id = clientID,
                name = msg.name,
                previousX = pos.x,
                previousY = pos.y,
                x = pos.x,
                y = pos.y,
                angle = 0f,
                state = State.IDLE,
                skin = Const.UI.randomSkin()))
            players[clientID] = player
            initializeBricks()
            return clientID
        }
        return null
    }

    fun onMove(msg: MoveMessage, clientID: UUID) {
        val player = onlinePlayers[clientID] ?: return
        if (player.model.state != State.KILLED) {
            player.model.x += msg.speedX
            player.model.y += msg.speedY
            player.model.angle = msg.angle
            player.model.state = State.valueOf(msg.stateName)
        }
        player.model.updated = true
    }

    fun onThrowBrick(msg: ThrowBrickMessage, clientID: UUID) {
        val thrower = onlinePlayers[clientID] ?: return
        if (thrower.model.hasBrick) {
            bricks += Brick(
                model = BrickModel(
                    id = UUID.randomUUID(),
                    angle = msg.angle,
                    x = msg.x,
                    y = msg.y,
                    previousX = msg.x,
                    previousY = msg.y,
                    hurting = true),
                initialSpeed = msg.force,
                currentSpeed = msg.force,
                throwerID = clientID)
            thrower.model.hasBrick = false
        }
    }

    fun onDisconnect(clientID: UUID): UUID? {
        val playerToRemove = players[clientID]
        val id = if (playerToRemove != null) {
            if (playerToRemove.model.hasBrick) {
                bricks += Brick(
                    model = BrickModel(
                        id = UUID.randomUUID(),
                        angle = 0.0,
                        x = playerToRemove.model.x,
                        y = playerToRemove.model.y,
                        previousX = playerToRemove.model.x,
                        previousY = playerToRemove.model.y,
                        hurting = false),
                    currentSpeed = 0f,
                    initialSpeed = 0f)
            }
            playerToRemove.isOnline = false
            logger.info("Client $clientID removed from the room")
            clientID
        } else {
            logger.warn("Client $clientID was disconnected, but there is no player with this id. Strange!")
            null
        }
        logger.info("RoomHandlers size: ${onlinePlayers.size}")
        return id
    }

    fun buildGameStateModel(): GameStateModel {
        return GameStateModel(
            players = onlinePlayers.values.map { it.model.copy() },
            bricks = bricks.map { it.model.copy() },
            bonuses = bonuses,
            time = time / 1000
        )
    }

    fun onTick(delta: Long): List<Pair<UUID, GameStateModel>> {
        if (onlinePlayers.isEmpty()) return emptyList()
        time += delta
        processGameState()
        val models = onlinePlayers.map { me ->
            Pair(me.key, buildGameStateModel())
        }

        onlinePlayers.forEach {
            it.value.model.previousX = it.value.model.x
            it.value.model.previousY = it.value.model.y
            it.value.model.updated = false
        }

        bricks.forEach {
            it.model.previousX = it.model.x
            it.model.previousY = it.model.y
        }

        return models
    }

    fun onSpawnBonus() {
        if (bonuses.size < Const.Balance.bonusesAtOnce && onlinePlayers.size > 1) {
            val pos = Const.Balance.randomSpawn()
            val typeName = Const.Balance.randomBonus()
            bonuses += BonusModel(UUID.randomUUID(), pos.x, pos.y, typeName)
        }
    }

    fun hasFreePlace(): Boolean {
        return onlinePlayers.size < maxPlayers
    }

    fun isShouldStop(): Boolean {
        return onlinePlayers.isEmpty() || !playing
    }

    private fun spawnBrick(): Brick {
        val pos = Const.Balance.randomSpawn()
        return Brick(
            model = BrickModel(
                id = UUID.randomUUID(),
                x = pos.x,
                y = pos.y,
                previousX = pos.x,
                previousY = pos.y,
                angle = 0.0,
                hurting = false),
            currentSpeed = 0f,
            initialSpeed = 0f)
    }

    private fun processGameState() {
        processBricks()
        processPlayers()
        processBonuses()
        processRoundTime()
    }

    private fun processRoundTime() {
        if (time > Const.Balance.roundTime) {
            playing = false
            onlinePlayers.keys.forEach {
                output.add(Message(
                    body = RoundEndsMessage(statistics = RoomStatisticsModel(
                        meId = it,
                        players = players.values.map {
                            ShortPlayerModel(
                                id = it.model.id,
                                deaths = it.model.deaths,
                                frags = it.model.frags,
                                name = it.model.name,
                                isOnline = it.isOnline
                            )
                        }
                    )),
                    to = listOf(it)
                ))
            }
        }
    }

    private fun processPlayers() {
        onlinePlayers.forEach { p1 ->
            if (p1.value.model.state != State.KILLED) {
                processPlayersIntersect(p1)
                processWallsIntersect(p1)
                processBricksIntersect(p1)
            }

            onlinePlayers.forEach { p ->
                p.value.model.x += p.value.moveVector.x
                p.value.model.y += p.value.moveVector.y
                p.value.moveVector.setZero()
            }
        }
    }

    private fun processPlayersIntersect(p1: Map.Entry<UUID, Player>) {
        onlinePlayers.filter { p2 ->
            p1.key != p2.key
                && p1.value.intersect(p2.value)
                && p2.value.model.state != State.KILLED
        }.forEach { p2 ->
            p1.value.moveVector.add(p1.value.pos.sub(p2.value.pos).setLength(Const.Balance.playerRadius - p1.value.pos.dst(p2.value.pos) / 2))
        }
    }

    private fun processWallsIntersect(p1: Map.Entry<UUID, Player>) {
        val wallShiftVector = Vector2()
            .add(intersect.leftWallPenetration(p1.value.model.x + p1.value.moveVector.x, Const.Balance.playerRadius),
                intersect.topWallPenetration(p1.value.model.y + p1.value.moveVector.y, Const.Balance.playerRadius))
            .sub(intersect.rightWallPenetration(p1.value.model.x + p1.value.moveVector.x, Const.Balance.playerRadius),
                intersect.bottomWallPenetration(p1.value.model.y + p1.value.moveVector.y, Const.Balance.playerRadius))

        p1.value.moveVector.add(wallShiftVector)
    }

    private fun processBricksIntersect(p1: Map.Entry<UUID, Player>) {
        val intersectedBricks = bricks.filter { brick ->
            (Vector2(p1.value.model.x, p1.value.model.y).dst(Vector2(brick.model.x, brick.model.y))
                < Const.Balance.playerRadius + Const.Balance.brickRadius)
        }

        if (intersectedBricks.isNotEmpty()) {
            val killerBricks = intersectedBricks.filter { brick ->
                brick.model.hurting
            }
            if (killerBricks.isNotEmpty()) {
                if (p1.value.model.bonuses.isNotEmpty()) {
                    p1.value.model.bonuses.clear()
                    killerBricks.forEach { brick ->
                        brick.model.angle += 180.0f
                    }
                } else {
                    p1.value.model.state = State.KILLED
                    p1.value.model.deaths += 1

                    val killers = killerBricks.map { brick ->
                        val throwerID = brick.throwerID
                        if (throwerID != null) {
                            val player = players[throwerID]
                            if (player != null) {
                                player.model.frags += 1
                            }
                            player
                        } else {
                            null
                        }
                    }.filterNotNull()

                    output.add(Message(KillMessage(
                        victimId = p1.value.model.id,
                        victimName = p1.value.model.name,
                        killerIds = killers.map { it.model.id },
                        killerNames = killers.map { it.model.name }
                    ), onlinePlayers.keys.toList()))

                    executor.deffer(2, TimeUnit.SECONDS, {
                        p1.value.model.state = State.IDLE
                    })
                }
            } else if (!p1.value.model.hasBrick) {
                p1.value.model.hasBrick = true
                bricks -= intersectedBricks.first()
            }
        }
    }

    private fun processBonuses() {
        onlinePlayers.values.forEach { player ->
            bonuses.filter {
                it.typeName == Const.Bonus.hat
            }.forEach { hat ->
                if (Vector2(hat.x, hat.y).dst(Vector2(player.model.x, player.model.y)) < (Const.Balance.playerRadius + Const.Balance.hatRadius)) {
                    player.model.bonuses += hat.typeName
                    bonuses -= hat
                }
            }
        }
    }

    private fun processBricks() {
        bricks.forEach { brick ->
            if (intersect.leftWallPenetration(brick.model.x, Const.Balance.brickRadius) > 0
                || intersect.rightWallPenetration(brick.model.x, Const.Balance.brickRadius) > 0
            ) {
                brick.model.angle = Vector2(Math.cos(Math.toRadians(brick.model.angle)).toFloat(), -1 * Math.sin(Math.toRadians(brick.model.angle)).toFloat()).angle()
            }
            if (intersect.topWallPenetration(brick.model.y, Const.Balance.brickRadius) > 0
                || intersect.bottomWallPenetration(brick.model.y, Const.Balance.brickRadius) > 0
            ) {
                brick.model.angle = Vector2(-1 * Math.cos(Math.toRadians(brick.model.angle)).toFloat(), Math.sin(Math.toRadians(brick.model.angle)).toFloat()).angle()
            }

            brick.model.x -= brick.currentSpeed * Math.sin(Math.toRadians(brick.model.angle)).toFloat()
            brick.model.y += brick.currentSpeed * Math.cos(Math.toRadians(brick.model.angle)).toFloat()

            if (brick.currentSpeed <= 0) {
                brick.currentSpeed = 0f
                brick.model.hurting = false
            } else {
                brick.currentSpeed -= Const.Balance.brickFriction
            }
        }
    }

    private fun initializeBricks() {
        if (onlinePlayers.size <= 1) {
            bricks += spawnBrick()
            bricks += spawnBrick()
        }
    }

    data class Brick(
        val model: BrickModel,
        var initialSpeed: Float,
        var currentSpeed: Float,
        var throwerID: UUID? = null
    )

    data class Player(
        val model: PlayerModel,
        val moveVector: Vector2 = Vector2(),
        var isOnline: Boolean = true
    ) {
        val pos: Vector2
            get() = Vector2(model.x, model.y)

        fun intersect(player: Player): Boolean {
            return pos.dst(player.pos) < (Const.Balance.playerRadius * 2)
        }
    }

    data class Message(
        val body: IMessage,
        val to: List<UUID>
    )
}
