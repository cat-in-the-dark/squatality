package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.IMessage
import com.catinthedark.lib.SimpleExecutor
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.models.*
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

class KryoService {
    private val logger = LoggerFactory.getLogger(KryoService::class.java)
    private val clientIdToUUID: MutableMap<Int, UUID> = hashMapOf()
    private val clientsInRoom: MutableMap<UUID, UUID> = hashMapOf()
    private val server: Server = Server()
    private val roomRegister = RoomRegister()
    private val executor: IExecutor = SimpleExecutor()
    private val events = object : ServerHandlers {
        override fun emit(msg: GameStartedMessage, clientID: UUID) {
            pushTCP(clientID, msg)
        }

        override fun emit(msg: GameStateMessage, clientID: UUID) {
            pushUDP(clientID, msg)
        }

        override fun emit(msg: EnemyConnectedMessage, clientID: UUID) {
            pushTCP(clientID, msg)
        }

        override fun emit(msg: EnemyDisconnectedMessage, clientID: UUID) {
            pushTCP(clientID, msg)
        }

    }
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
            val clientID = clientIdToUUID[connection.id] ?: return
            val roomID = clientsInRoom[clientID] ?: return
            clientsInRoom.remove(clientID)
            roomRegister[roomID]?.invoke(RoomHandlers::onDisconnect, clientID)
            logger.info("Disconnected client $clientID")
        }

        override fun received(connection: Connection, data: Any?) {
            if (data == null || data !is String) {
                logger.debug("Can't handle message $data")
                return
            }

            try {
                val clientID = clientIdToUUID[connection.id] ?: return
                val msg = MessageConverter.parser.unwrap(data)
                val room = roomRegister[clientsInRoom[clientID]] ?: return
                when (msg) {
                    is HelloMessage -> room.invoke(RoomHandlers::onHello, msg, clientID)
                    is MoveMessage -> room.invoke(RoomHandlers::onMove, msg, clientID)
                    is ThrowBrickMessage -> room.invoke(RoomHandlers::onThrowBrick, msg, clientID)
                    else -> logger.warn("Undefined message $msg")
                }
            } catch (e: Exception) {
                logger.error("Can't handle message $data: ${e.message}", e)
            }
        }
    }

    private fun findOrCreateRoom(callback: (UUID) -> Unit) {
        val rooms = clientsInRoom.values.toSet()
        if (rooms.isEmpty()) {
            val roomID = UUID.randomUUID()
            roomRegister.register(roomID, events, executor)
            callback(roomID)
        } else {
            callback(rooms.first())
        }
    }

    /**
     * Syntax sugar over sendEvent.
     * So you don't have to call MessageConverter manually!!!
     */
    fun pushTCP(clientID: UUID, msg: IMessage?) {
        if (msg == null) return
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
    fun pushUDP(clientID: UUID, msg: IMessage?) {
        if (msg == null) return
        val id = clientIdToUUID.filterValues { it == clientID }.keys.firstOrNull()
        if (id == null) {
            logger.warn("Can't find client with id $clientID")
            return
        }
        server.sendToUDP(id, MessageConverter.parser.wrap(msg))
    }

    fun start() {
        server.addListener(listener)
        server.start()
        server.bind(Configs.tcpPort, Configs.udpPort)

        var lastTick = System.nanoTime()
        executor.periodic(Const.Network.Server.tickDelay, TimeUnit.MILLISECONDS, {
            val currentTime = System.nanoTime()
            val delta = (currentTime - lastTick) / 1000000
            roomRegister.forEach { it.invoke(RoomHandlers::onTick, delta) }
            lastTick = currentTime
        })
    }

    fun stop() {
        server.removeListener(listener)
    }
}
