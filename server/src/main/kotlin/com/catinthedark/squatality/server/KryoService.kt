package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.IMessage
import com.catinthedark.lib.SimpleExecutor
import com.catinthedark.lib.invoker.InvokeWrapper
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.models.*
import com.catinthedark.squatality.server.spy.SpyService
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import com.esotericsoftware.kryonet.Server
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.*

class KryoService {
    private val logger = LoggerFactory.getLogger(KryoService::class.java)
    private val clientIdToUUID: MutableMap<Int, UUID> = hashMapOf()
    private val clientsInRoom: MutableMap<UUID, UUID> = hashMapOf()
    private val server: Server = Server()
    private val serviceExecutor: Executor = Executors.newSingleThreadExecutor()
    private val ger: ServerGameEventsRegistrar = ServerGameEventsRegistrar(serviceExecutor)
    private val spyService: SpyService = SpyService(ger)
    private val roomRegister = RoomRegister(ger)
    private val coreExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private val executor: IExecutor = SimpleExecutor(coreExecutor)
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
            is RoundEndsMessage -> pushTCP(clientID, msg)
            else -> logger.warn("Unknown message $msg")
        }
    }

    private val disconnect: (clientID: UUID) -> Unit = { clientID ->
        val id = clientIdToUUID.filterValues { it == clientID }.keys.firstOrNull()
        server.connections.find { it.id == id }?.close()
    }

    private val listener = object : Listener() {
        override fun connected(connection: Connection) {
            val clientID = UUID.randomUUID()
            clientIdToUUID[connection.id] = clientID
            pushTCP(clientID, ServerHelloMessage(clientID))
            logger.info("Connected client $clientID")
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
                when (data) {
                    is HelloMessage -> {
                        findOrCreateRoom(clientID, data, connection.remoteAddressTCP)
                    }
                    is MoveMessage -> {
                        val room = roomRegister[clientsInRoom[clientID]]
                        room?.invoke(RoomHandlers::onMove, data, clientID)
                    }
                    is ThrowBrickMessage -> {
                        val room = roomRegister[clientsInRoom[clientID]]
                        room?.invoke(RoomHandlers::onThrowBrick, data, clientID)
                    }
                    else -> logger.warn("Undefined message $data")
                }
            } catch (e: Exception) {
                logger.error("Can't handle message $data: ${e.message}", e)
            }
        }
    }

    private fun findOrCreateRoom(clientID: UUID, data: HelloMessage, remoteAddressTCP: InetSocketAddress?) {
        connectOrCreateRoom(clientID, data, remoteAddressTCP?.hostString)
    }

    private fun createRoom(clientID: UUID, data: HelloMessage, address: String?) {
        val roomID = UUID.randomUUID()
        roomRegister.register(roomID, publish, disconnect, coreExecutor)
        val room = roomRegister[roomID]
        if (room?.invoke(RoomHandlers::onHello, data, clientID, address)?.get() != null) {
            clientsInRoom[clientID] = roomID
        }
    }

    private fun connectOrCreateRoom(clientID: UUID, data: HelloMessage, address: String?) {
        val rooms = clientsInRoom.values.toSet()
        rooms.forEach { roomID ->
            val room = roomRegister[roomID]
            if (room?.invoke(RoomHandlers::onHello, data, clientID, address)?.get() != null) {
                clientsInRoom[clientID] = roomID
                return
            }
        }
        createRoom(clientID, data, address)
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
        spyService.register()
    }

    fun stop() {
        server.removeListener(listener)
        spyService.dispose()
    }
}
