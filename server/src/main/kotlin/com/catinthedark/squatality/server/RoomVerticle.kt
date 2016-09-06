package com.catinthedark.squatality.server

import io.vertx.core.AbstractVerticle
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

        vertx.eventBus().consumer<JsonObject>(Addressing.onMove(id), {moveHandler(it)})
        vertx.eventBus().consumer<JsonObject>(Addressing.onConnect(id), {connectHandler(it)})
        vertx.eventBus().consumer<JsonObject>(Addressing.onDisconnect(id), {disconnectHandler(it)})
        vertx.eventBus().consumer<Long>(Addressing.onTick(), {tickHandler(it)})
    }

    override fun stop() {
        logger.info("Room stopped")
    }

    private fun tickHandler(time: Message<Long>) {
        logger.info("Room-$id onTick: ${time.body()}")
    }

    private fun connectHandler(msg: Message<JsonObject>) {
        logger.info("Room-$id onConnect: ${msg.body()}")
    }

    private fun disconnectHandler(msg: Message<JsonObject>) {
        logger.info("Room-$id onDisconnect: ${msg.body()}")
        vertx.undeploy(deploymentID()) // TODO: example!
    }

    private fun moveHandler(msg: Message<JsonObject>) {
        logger.info("Room-$id moveHandler: $msg")
    }
}