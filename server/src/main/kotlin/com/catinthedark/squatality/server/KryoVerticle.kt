package com.catinthedark.squatality.server

import com.catinthedark.lib.IMessage
import com.catinthedark.squatality.models.*
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.util.*

class KryoVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(KryoVerticle::class.java)
    private val clientIdToUUID: MutableMap<Int, UUID> = hashMapOf()
    private val clientsInRoom: MutableMap<UUID, UUID> = hashMapOf()
    private val server: Server = Server()
    private val listener = object : Listener() {
        override fun connected(connection: Connection) {
            val clientID = UUID.randomUUID()
            clientIdToUUID[connection.id] = clientID

            findOrCreateRoom { id ->
                clientsInRoom[clientID] = id
                pushTCP(clientID, ServerHelloMessage(clientID))
                logger.info("Connected client $clientID")
            }
        }

        override fun disconnected(connection: Connection) {
            val clientID = clientIdToUUID[connection.id]
            val roomID = clientsInRoom[clientID]
            if (roomID != null) {
                clientsInRoom.remove(clientID)
                vertx.eventBus().send(Addressing.onDisconnect(roomID), clientID.toString())
            }
            logger.info("Disconnected client $clientID")
        }

        override fun received(connection: Connection, data: Any?) {
            if (data == null || data !is String) {
                logger.warn("Can't handle message $data")
                return
            }

            try {
                val clientID = clientIdToUUID[connection.id]
                val msg = MessageConverter.parser.unwrap(data)
                val roomID = clientsInRoom[clientID]
                if (roomID != null) {
                    val options = DeliveryOptions()
                    options.addHeader(headerClientID, clientID.toString())
                    with(vertx.eventBus(), {
                        when (msg) {
                            is HelloMessage -> publish(Addressing.onHello(roomID), msg, options)
                            is MoveMessage -> publish(Addressing.onMove(roomID), msg, options)
                            is ThrowBrickMessage -> publish(Addressing.onThrowBrick(roomID), msg, options)
                            else -> logger.warn("Undefined message $msg")
                        }
                    })
                }
            } catch (e: Exception) {
                logger.error("Can't handle message $data: ${e.message}", e)
            }
        }
    }

    private fun findOrCreateRoom(callback: (UUID) -> Unit) {
        val rooms = clientsInRoom.values.toSet()
        if (rooms.size < 1) {
            val roomID = UUID.randomUUID()
            val roomConfig = JsonObject().put("uuid", roomID.toString())
            val roomOptions = DeploymentOptions().setConfig(roomConfig)
            vertx.deployVerticle(RoomVerticle(), roomOptions, {
                callback(roomID)
            })
        } else {
            callback(rooms.first())
        }
    }

    /**
     * Syntax sugar over sendEvent.
     * So you don't have to call MessageConverter manually!!!
     */
    fun pushTCP(clientID: UUID?, msg: IMessage?) {
        if (msg == null) return
        if (clientID == null) {
            logger.warn("ClientID is null")
            return
        }
        val id = clientIdToUUID.filterValues { it == clientID }.keys.firstOrNull()
        if (id == null) {
            logger.warn("Can't find client with id $clientID")
            return
        }
        server.sendToTCP(id, MessageConverter.parser.wrap(msg))
    }

    /**
     * Syntax sugar over sendEvent.
     * So you don't have to call MessageConverter manually!!!
     */
    fun pushUDP(clientID: UUID?, msg: IMessage?) {
        if (msg == null) return
        if (clientID == null) {
            logger.warn("ClientID is null")
            return
        }
        val id = clientIdToUUID.filterValues { it == clientID }.keys.firstOrNull()
        if (id == null) {
            logger.warn("Can't find client with id $clientID")
            return
        }
        server.sendToUDP(id, MessageConverter.parser.wrap(msg))
    }

    override fun start() {
        server.start()
        server.bind(Configs.tcpPort, Configs.udpPort)
        server.addListener(listener)

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
            pushTCP(clientID, it.body())
        })
        vertx.eventBus().localConsumer<GameStateMessage>(Addressing.onGameState(), {
            val clientID = clientFromHeaders(it)
            pushUDP(clientID, it.body())
        })
        vertx.eventBus().localConsumer<EnemyConnectedMessage>(Addressing.onEnemyConnected(), {
            val clientID = clientFromHeaders(it)
            pushTCP(clientID, it.body())
        })
        vertx.eventBus().localConsumer<EnemyDisconnectedMessage>(Addressing.onEnemyDisconnected(), {
            val clientID = clientFromHeaders(it)
            pushTCP(clientID, it.body())
        })
    }

    private fun <T : IMessage> clientFromHeaders(msg: Message<T>): UUID? {
        val id = msg.headers().get(headerClientID) ?: return null
        return UUID.fromString(id)
    }
}
