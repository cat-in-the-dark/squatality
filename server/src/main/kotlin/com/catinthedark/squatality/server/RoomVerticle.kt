package com.catinthedark.squatality.server

import com.catinthedark.lib.IMessage
import com.catinthedark.models.GameStartedMessage
import com.catinthedark.models.HelloMessage
import com.catinthedark.models.MoveMessage
import com.catinthedark.models.ThrowBrickMessage
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.logging.LoggerFactory
import java.util.*

/**
 * This class is central handler for all room-specific messages coming from clients.
 * In thread safe eventBus we trust.
 *
 * Every room has UUID id to subscribe on dynamically created message handlers.
 */
class RoomVerticle: AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(RoomVerticle::class.java)!!
    lateinit var id: UUID
    private lateinit var service: RoomService

    override fun start() {
        id = UUID.fromString(config().getString("uuid"))!!
        logger.info("Room-$id started")
        service = RoomService()

        vertx.eventBus().consumer<MoveMessage>(Addressing.onMove(id), {moveHandler(it)})
        vertx.eventBus().consumer<HelloMessage>(Addressing.onHello(id), {helloHandler(it)})
        vertx.eventBus().consumer<ThrowBrickMessage>(Addressing.onThrowBrick(id), {throwBrickHandler(it)})
        vertx.eventBus().consumer<String>(Addressing.onDisconnect(id), {disconnectHandler(it)})
        vertx.eventBus().consumer<Long>(Addressing.onTick(), {tickHandler(it)})
    }

    override fun stop() {
        logger.info("Room-$id stopped")
    }

    private fun helloHandler(msg: Message<HelloMessage>) {
        val clientID = clientFromHeaders(msg)
        service.onNewClient(msg.body()!!, clientID)
        sendToClient(Addressing.onGameStarted(), GameStartedMessage(clientID), clientID)
    }

    private fun throwBrickHandler(msg: Message<ThrowBrickMessage>) {
        val clientID = clientFromHeaders(msg)
        service.onThrowBrick(msg.body()!!, clientID)
    }

    private fun moveHandler(msg: Message<MoveMessage>) {
        val clientID = clientFromHeaders(msg)
        service.onMove(msg.body()!!, clientID)
    }

    private fun tickHandler(delta: Message<Long>) {
        service.onTick(delta.body())
    }

    private fun disconnectHandler(msg: Message<String>) {
        logger.info("Room-$id onDisconnect: ${msg.body()}")
        val clientID = UUID.fromString(msg.body() ?: return)
        service.onDisconnect(clientID)
        if (service.shouldStop()) {
            vertx.undeploy(deploymentID())
        }
    }

    private fun <T: IMessage> clientFromHeaders(msg: Message<T>): UUID {
        return UUID.fromString(msg.headers()[headerClientID]!!)
    }

    private fun <T: IMessage> sendToClient(address: String, msg: T, clientID: UUID) {
        val options = DeliveryOptions()
        options.addHeader(headerClientID, clientID.toString())
        vertx.eventBus().send(address, msg, options)
    }
}
