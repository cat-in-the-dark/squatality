package com.catinthedark.squatality.server

import com.catinthedark.Const
import com.catinthedark.lib.IExecutor
import com.catinthedark.math.Vector2
import com.catinthedark.models.*
import com.catinthedark.squatality.server.math.IntersectService
import io.vertx.core.logging.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This class supposed to work in single thread.
 * So we do not need synchronized and concurrent collections any more.
 */
class RoomService(
    private val executor: IExecutor
) {
    private val logger = LoggerFactory.getLogger(RoomService::class.java)!!
    private val players: MutableMap<UUID, Player> = hashMapOf()
    private val bonuses: MutableList<BonusModel> = arrayListOf()
    private val bricks: MutableList<Brick> = arrayListOf()
    private var time: Long = 0
    private val intersect = IntersectService()

    fun onNewClient(msg: HelloMessage, clientID: UUID) {
        if (hasFreePlace()) {
            val pos = Const.Balance.randomSpawn()
            val player = Player(PlayerModel(
                id = UUID.randomUUID(),
                name = msg.name,
                x = pos.x,
                y = pos.y,
                angle = 0f,
                state = State.IDLE,
                skin = Const.UI.randomSkin()))
            players[clientID] = player
            initializeBricks()
        }
    }

    fun onMove(msg: MoveMessage, clientID: UUID) {
        val player = players[clientID] ?: return
        if (player.model.state != State.KILLED) {
            player.model.x += msg.speedX
            player.model.y += msg.speedY
            player.model.angle = msg.angle
            player.model.state = State.valueOf(msg.stateName)
        }
    }

    fun onThrowBrick(msg: ThrowBrickMessage, clientID: UUID) {
        val thrower = players[clientID] ?: return
        if (thrower.model.hasBrick) {
            bricks += Brick(
                model = BrickModel(
                    id = UUID.randomUUID(),
                    angle = msg.angle,
                    x = msg.x,
                    y = msg.y,
                    hurting = true),
                initialSpeed = msg.force,
                currentSpeed = msg.force,
                throwerID = clientID)
            thrower.model.hasBrick = false
        }
    }

    fun onDisconnect(clientID: UUID) {
        logger.info("Disconnected $clientID")
        val playerToRemove = players[clientID]
        if (playerToRemove != null) {
            if (playerToRemove.model.hasBrick) {
                bricks += Brick(
                    model = BrickModel(
                        id = UUID.randomUUID(),
                        angle = 0.0,
                        x = playerToRemove.model.x,
                        y = playerToRemove.model.y,
                        hurting = false),
                    currentSpeed = 0f,
                    initialSpeed = 0f)
            }
            players.remove(clientID)
        }
        logger.info("Room size: ${players.size}")
    }

    fun onTick(delta: Long): List<Pair<UUID, GameStateModel>> {
        if (players.isEmpty()) return emptyList()
        time += delta
        processGameState()
        return players.map { me ->
            Pair(me.key, GameStateModel(
                me = me.value.model,
                players = players.filter { me.key != it.key }.values.map { it.model },
                bricks = bricks.map { it.model },
                bonuses = bonuses,
                time = time / 1000
            ))
        }
    }

    fun onSpawnBonus() {
        if (bonuses.size < Const.Balance.bonusesAtOnce && players.size > 1) {
            val pos = Const.Balance.randomSpawn()
            val typeName = Const.Balance.randomBonus()
            bonuses += BonusModel(UUID.randomUUID(), pos.x, pos.y, typeName)
            println("Bonuses: $bonuses")
        }
    }

    fun hasFreePlace(): Boolean {
        return true
    }

    fun shouldStop(): Boolean {
        return players.isEmpty()
    }

    private fun spawnBrick(): Brick {
        val pos = Const.Balance.randomBrickSpawn()
        return Brick(
            model = BrickModel(
                id = UUID.randomUUID(),
                x = pos.x,
                y = pos.y,
                angle = 0.0,
                hurting = false),
            currentSpeed = 0f,
            initialSpeed = 0f)
    }

    private fun processGameState() {
        processBricks()
        processPlayers()
        processBonuses()
    }

    private fun processPlayers() {
        players.forEach { p1 ->
            if (!p1.value.model.state.equals(State.KILLED)) {
                players.filter { p2 ->
                    !p1.key.equals(p2.key)
                        && p1.value.intersect(p2.value)
                        && !p2.value.model.state.equals(State.KILLED)
                }.forEach { p2 ->
                    p1.value.moveVector.add(p1.value.pos.sub(p2.value.pos).setLength(Const.Balance.playerRadius - p1.value.pos.dst(p2.value.pos) / 2))
                }

                val wallShiftVector = Vector2()
                    .add(intersect.leftWallPenetration(p1.value.model.x + p1.value.moveVector.x, Const.Balance.playerRadius),
                        intersect.topWallPenetration(p1.value.model.y + p1.value.moveVector.y, Const.Balance.playerRadius))
                    .sub(intersect.rightWallPenetration(p1.value.model.x + p1.value.moveVector.x, Const.Balance.playerRadius),
                        intersect.bottomWallPenetration(p1.value.model.y + p1.value.moveVector.y, Const.Balance.playerRadius))

                p1.value.moveVector.add(wallShiftVector)

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
                            players.values.forEach { player ->
                                // TODO: sounds have to reach clients
                                val sound = SoundMessage(
                                    when (Random().nextInt(10)) {
                                        1 -> SoundName.ChponkSuka
                                        2 -> SoundName.Tooth
                                        else -> SoundName.HeadShot
                                    })
                            }
                            killerBricks.forEach { brick ->
                                val throwerID = brick.throwerID
                                if (throwerID != null) {
                                    val player = players[throwerID]
                                    if (player != null) {
                                        player.model.frags += 1
                                    }
                                }
                            }
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

            players.forEach { p ->
                p.value.model.x += p.value.moveVector.x
                p.value.model.y += p.value.moveVector.y
                p.value.moveVector.setZero()
            }
        }
    }

    private fun processBonuses() {
        players.values.forEach { player ->
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
        if (players.size <= 1) {
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
        val pos: Vector2 = Vector2(model.x, model.y)
    ) {
        fun intersect(player: Player): Boolean =
            pos.dst(player.pos) < (Const.Balance.playerRadius * 2)
    }
}
