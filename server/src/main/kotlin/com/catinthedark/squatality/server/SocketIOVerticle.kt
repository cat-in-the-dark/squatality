package com.catinthedark.squatality.server

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.util.*

class SocketIOVerticle: AbstractVerticle() {
    val logger = LoggerFactory.getLogger(SocketIOVerticle::class.java)!!
    val config: Configuration
    val server: SocketIOServer

    val clientsInRoom: MutableMap<UUID, UUID> = hashMapOf()

    init {
        config = Configuration()
        config.port = 8080
        config.socketConfig.isReuseAddress = true
        config.isUseLinuxNativeEpoll
        server = SocketIOServer(config)
    }

    private val connectHandler = ConnectListener { client ->
        val roomID = UUID.randomUUID()
        clientsInRoom[client.sessionId] = roomID
        val roomConfig = JsonObject().put("uuid", roomID.toString())
        val roomOptions = DeploymentOptions().setConfig(roomConfig)
        vertx.deployVerticle(RoomVerticle(), roomOptions, {
            if (it.succeeded()) {
                vertx.eventBus().send(Addressing.onConnect(roomID), JsonObject())
            }
        })
        client.sendEvent(eventName, "HELLO")
    }

    private val disconnectHandler = DisconnectListener { client ->
        val roomID = clientsInRoom[client.sessionId]
        if (roomID != null) {
            clientsInRoom.remove(roomID)
            vertx.eventBus().send(Addressing.onDisconnect(roomID), JsonObject())
        }
    }

    private val messageHandler = DataListener<String> { client, data, ackRequest ->
        val roomID = clientsInRoom[client.sessionId]
        if (roomID != null) {
            vertx.eventBus().send(Addressing.onMove(roomID), JsonObject())
        }
    }

    override fun start() {
        server.addConnectListener(connectHandler)
        server.addDisconnectListener(disconnectHandler)
        server.addEventListener(eventName, String::class.java, messageHandler)
        server.startAsync().addListener {
            if (it.isSuccess) {
                logger.info("Server started!!")
            } else {
                logger.error("Can't start server", it.cause())
            }
        }
        vertx.setPeriodic(tickDelay, {
            vertx.eventBus().publish(Addressing.onTick(), System.nanoTime())
        })
    }

    override fun stop() {
        server.stop()
        logger.info("Server stopped")
    }
}