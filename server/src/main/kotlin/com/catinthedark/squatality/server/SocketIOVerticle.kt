package com.catinthedark.squatality.server

import com.catinthedark.lib.IMessage
import com.catinthedark.models.*
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.listener.DisconnectListener
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.eventbus.DeliveryOptions
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
        vertx.deployVerticle(RoomVerticle(), roomOptions)

        client.push(ServerHelloMessage(client.sessionId))
    }

    private val disconnectHandler = DisconnectListener { client ->
        val roomID = clientsInRoom[client.sessionId]
        if (roomID != null) {
            clientsInRoom.remove(roomID)
            vertx.eventBus().send(Addressing.onDisconnect(roomID), JsonObject())
        }
    }

    private val messageHandler = DataListener<String> { client, data, ackRequest ->
        try {
            val msg = MessageConverter.parser.unwrap(data)
            logger.info(msg)
            val roomID = clientsInRoom[client.sessionId]
            if (roomID != null) {
                val options = DeliveryOptions()
                options.addHeader(headerClientID, client.sessionId.toString())
                with(vertx.eventBus(), {
                    when (msg) {
                        is HelloMessage -> send(Addressing.onHello(roomID), msg, options)
                        is MoveMessage -> send(Addressing.onMove(roomID), msg, options)
                        is ThrowBrickMessage -> send(Addressing.onThrowBrick(roomID), msg, options)
                        else -> logger.warn("Undefined message $msg")
                    }
                })
            }
        } catch (e: Exception) {
            logger.error("Can't handle message: ${e.message}", e)
        }
    }

    fun SocketIOClient.push(msg: IMessage) {
        sendEvent(eventName, MessageConverter.parser.wrap(msg))
    }

    override fun start() {
        registerCodecs(vertx.eventBus())
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
        vertx.eventBus().consumer<GameStartedMessage>(Addressing.onGameStarted(), {
            val clientID = UUID.fromString(it.headers()[headerClientID])
            server.getClient(clientID).push(it.body()!!)
        })
        vertx.eventBus().consumer<GameStateMessage>(Addressing.onGameState(), {
            val clientID = UUID.fromString(it.headers()[headerClientID])
            server.getClient(clientID).push(it.body()!!)
        })
        vertx.setPeriodic(tickDelay, {
            vertx.eventBus().publish(Addressing.onTick(), System.nanoTime())
        })
    }

    override fun stop() {
        server.stop()
        logger.info("Server stopped")
    }
}
