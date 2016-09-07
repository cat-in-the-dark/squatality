package com.catinthedark.squatality.server

import com.catinthedark.lib.IMessage
import com.catinthedark.models.GameStartedMessage
import com.catinthedark.models.HelloMessage
import com.catinthedark.models.MoveMessage
import com.catinthedark.models.ThrowBrickMessage
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.util.*

class RoomVerticle: AbstractVerticle() {
    val logger = LoggerFactory.getLogger(RoomVerticle::class.java)!!
    lateinit var id: UUID

    override fun start() {
        id = UUID.fromString(config().getString("uuid"))!!
        logger.info("Room started $id")

        vertx.eventBus().consumer<MoveMessage>(Addressing.onMove(id), {moveHandler(it)})
        vertx.eventBus().consumer<HelloMessage>(Addressing.onHello(id), {helloHandler(it)})
        vertx.eventBus().consumer<ThrowBrickMessage>(Addressing.onThrowBrick(id), {throwBrickHandler(it)})
        vertx.eventBus().consumer<JsonObject>(Addressing.onConnect(id), {connectHandler(it)})
        vertx.eventBus().consumer<JsonObject>(Addressing.onDisconnect(id), {disconnectHandler(it)})
        vertx.eventBus().consumer<Long>(Addressing.onTick(), {tickHandler(it)})
    }

    override fun stop() {
        logger.info("Room stopped")
    }

    private fun helloHandler(msg: Message<HelloMessage>) {
        logger.info("Room-$id helloHandler: ${msg.body()}")
        val clientID = clientFromHeaders(msg)
        sendToClient(Addressing.onGameStarted(), GameStartedMessage(clientID), clientID)
    }

    private fun throwBrickHandler(msg: Message<ThrowBrickMessage>) {
        logger.info("Room-$id throwBrickHandler: ${msg.body()}")
        val clientID = clientFromHeaders(msg)
    }

    private fun moveHandler(msg: Message<MoveMessage>) {
        logger.info("Room-$id moveHandler: ${msg.body()}")
        val clientID = clientFromHeaders(msg)
    }

    private fun tickHandler(time: Message<Long>) {
    }

    private fun connectHandler(msg: Message<JsonObject>) {
        logger.info("Room-$id onConnect: ${msg.body()}")
    }

    private fun disconnectHandler(msg: Message<JsonObject>) {
        logger.info("Room-$id onDisconnect: ${msg.body()}")
        vertx.undeploy(deploymentID()) // TODO: example!
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
