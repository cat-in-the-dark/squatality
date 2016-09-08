package com.catinthedark.squatality.server

import com.catinthedark.models.*
import io.vertx.core.logging.LoggerFactory
import java.util.*

/**
 * This class supposed to work in single thread.
 * So we do not need synchronized and concurrent collections any more.
 */
class RoomService {
    private val logger = LoggerFactory.getLogger(RoomService::class.java)!!
    private val clients: MutableMap<UUID, PlayerModel> = hashMapOf()
    private var time: Long = 0
    val clientsIDs: Set<UUID>
        get() = clients.keys.toSet()

    fun onNewClient(msg: HelloMessage, clientID: UUID) {
        if (hasFreePlace()) {
            val player = PlayerModel(
                id = UUID.randomUUID(),
                name = msg.name,
                x = 100f, y = 100f,
                angle = 0f,
                state = State.IDLE,
                skin = "gop_blue"
            )
            clients[clientID] = player
        }
    }

    fun onMove(msg: MoveMessage, clientID: UUID) {
        val player = clients[clientID] ?: return
        player.x += msg.speedX
        player.y += msg.speedY
        player.angle = msg.angle
    }

    fun onThrowBrick(msg: ThrowBrickMessage, clientID: UUID) {

    }

    fun onDisconnect(clientID: UUID) {
        logger.info("Disconnected $clientID")
        clients.remove(clientID)
        logger.info("Room size: ${clients.size}")
    }

    fun onTick(delta: Long): GameStateModel? {
        if (clients.isEmpty()) return null
        time += delta
        return GameStateModel(
            me = clients.values.first(),
            players = emptyList(),
            bricks = emptyList(),
            bonuses = emptyList(),
            time = time / 1000)
    }

    fun hasFreePlace(): Boolean {
        return true
    }

    fun shouldStop(): Boolean {
        return clients.isEmpty()
    }
}
