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
    /**
     * We need this to make RoomService unaware of transport system - udp or tcp.
     */
    private val publish: (IMessage, UUID) -> Unit = { msg, clientID ->
        when (msg) {
            is GameStartedMessage -> pushTCP(clientID, msg)
            is GameStateMessage -> pushUDP(clientID, msg)
            is EnemyConnectedMessage -> pushTCP(clientID, msg)
            is EnemyDisconnectedMessage -> pushTCP(clientID, msg)
            is KillMessage -> pushTCP(clientID, msg)
            else -> logger.warn("Unknown message $msg")
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
            if (data == null || data !is IMessage) {
                logger.debug("Can't handle message $data")
                return
            }

            try {
                val clientID = clientIdToUUID[connection.id] ?: return
                val room = roomRegister[clientsInRoom[clientID]] ?: return
                when (data) {
                    is HelloMessage -> room.invoke(RoomHandlers::onHello, data, clientID)
                    is MoveMessage -> room.invoke(RoomHandlers::onMove, data, clientID)
                    is ThrowBrickMessage -> room.invoke(RoomHandlers::onThrowBrick, data, clientID)
                    else -> logger.warn("Undefined message $data")
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
            roomRegister.register(roomID, publish, executor)
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
        server.sendToTCP(id, msg)
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
        server.sendToUDP(id, msg)
    }

    fun start() {
        server.addListener(listener)
        server.start()
        server.bind(Configs.tcpPort, Configs.udpPort)

        MessageConverter.kryoRegister(server.kryo)

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
