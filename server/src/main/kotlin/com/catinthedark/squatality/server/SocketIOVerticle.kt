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
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.util.*

class SocketIOVerticle : AbstractVerticle() {
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

    private fun findOrCreateRoom(): UUID {
        val rooms = clientsInRoom.values.toSet()
        if (rooms.size < 1) {
            val roomID = UUID.randomUUID()
            val roomConfig = JsonObject().put("uuid", roomID.toString())
            val roomOptions = DeploymentOptions().setConfig(roomConfig)
            vertx.deployVerticle(RoomVerticle(), roomOptions)
            return roomID
        } else {
            return rooms.first()
        }
    }

    private val connectHandler = ConnectListener { client ->
        clientsInRoom[client.sessionId] = findOrCreateRoom()
        push(client, ServerHelloMessage(client.sessionId))
        logger.info("Connected client ${client.sessionId}")
    }

    private val disconnectHandler = DisconnectListener { client ->
        val roomID = clientsInRoom[client.sessionId]
        if (roomID != null) {
            clientsInRoom.remove(client.sessionId)
            vertx.eventBus().send(Addressing.onDisconnect(roomID), client.sessionId.toString())
        }
        logger.info("Disconnected client ${client.sessionId}")
    }

    private val messageHandler = DataListener<String> { client, data, ackRequest ->
        try {
            val msg = MessageConverter.parser.unwrap(data)
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
            logger.error("Can't handle message $data: ${e.message}", e)
        }
    }

    /**
     * Syntax sugar over sendEvent.
     * So you don't have to call MessageConverter manually!!!
     */
    fun push(client: SocketIOClient?, msg: IMessage?) {
        if (msg == null) return
        if (client == null) {
            logger.warn("SocketIOClient is null")
            return
        }
        client.sendEvent(eventName, MessageConverter.parser.wrap(msg))
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

        registerReverseHandler()
        var lastTick = System.nanoTime()
        vertx.setPeriodic(tickDelay, {
            val currentTime = System.nanoTime()
            vertx.eventBus().publish(Addressing.onTick(), (currentTime - lastTick) / 1000000)
            lastTick = currentTime
        })
    }

    /**
     * This handlers will be called from RoomVerticle.
     * We need this to make RoomVerticle unaware of socketIO system.
     */
    private fun registerReverseHandler() {
        vertx.eventBus().localConsumer<GameStartedMessage>(Addressing.onGameStarted(), {
            val clientID = clientFromHeaders(it)
            push(server.getClient(clientID), it.body())
        })
        vertx.eventBus().localConsumer<GameStateMessage>(Addressing.onGameState(), {
            val clientID = clientFromHeaders(it)
            push(server.getClient(clientID), it.body())
        })
    }

    private fun <T : IMessage> clientFromHeaders(msg: Message<T>): UUID? {
        val id = msg.headers().get(headerClientID) ?: return null
        return UUID.fromString(id)
    }

    override fun stop() {
        server.stop()
        logger.info("Server stopped")
    }
}
