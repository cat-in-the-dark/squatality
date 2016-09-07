package com.catinthedark.squatality.server

import com.catinthedark.models.HelloMessage
import com.catinthedark.models.MoveMessage
import com.catinthedark.models.PlayerModel
import com.catinthedark.models.ThrowBrickMessage
import io.vertx.core.logging.LoggerFactory
import java.util.*

/**
 * This class supposed to work in single thread.
 * So we do not need synchronized and concurrent collections any more.
 */
class RoomService {
    private val logger = LoggerFactory.getLogger(RoomService::class.java)!!
    private val clients: MutableMap<UUID, PlayerModel> = hashMapOf()

    fun onNewClient(msg: HelloMessage, clientID: UUID) {

    }

    fun onMove(msg: MoveMessage, clientID: UUID) {

    }

    fun onThrowBrick(msg: ThrowBrickMessage, clientID: UUID) {

    }

    fun onDisconnect(clientID: UUID) {

    }

    fun onTick(delta: Long) {

    }

    fun hasFreePlace(): Boolean {
        return true
    }

    fun shouldStop(): Boolean {
        return clients.isEmpty()
    }
}
