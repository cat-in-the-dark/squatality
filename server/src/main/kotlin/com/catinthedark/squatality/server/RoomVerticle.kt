package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.IMessage
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.models.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.logging.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This class is central handler for all room-specific messages coming from clients.
 * In thread safe eventBus we trust.
 *
 * Every room has UUID id to subscribe on dynamically created message handlers.
 */
class RoomVerticle: AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(RoomVerticle::class.java)!!
    lateinit var roomID: UUID
    private lateinit var executor: IExecutor
    private lateinit var service: RoomService

    override fun start() {
        roomID = UUID.fromString(config().getString("uuid"))!!
        logger.info("Room-$roomID started")
        executor = VertxExecutor(vertx)
        service = RoomService(executor)

        vertx.eventBus().localConsumer<MoveMessage>(Addressing.onMove(roomID), {moveHandler(it)})
        vertx.eventBus().localConsumer<HelloMessage>(Addressing.onHello(roomID), {helloHandler(it)})
        vertx.eventBus().localConsumer<ThrowBrickMessage>(Addressing.onThrowBrick(roomID), {throwBrickHandler(it)})
        vertx.eventBus().localConsumer<String>(Addressing.onDisconnect(roomID), {disconnectHandler(it)})
        vertx.eventBus().localConsumer<Long>(Addressing.onTick(), {tickHandler(it)})

        executor.periodic(Const.Balance.bonusDelay, TimeUnit.SECONDS, {
            service.onSpawnBonus()
        })
    }

    override fun stop() {
        logger.info("Room-$roomID stopped")
    }

    private fun helloHandler(msg: Message<HelloMessage>) {
        val clientID = clientFromHeaders(msg) ?: return
        val body = msg.body() ?: return
        val id = service.onNewClient(body, clientID) ?: return
        val gsm = service.buildGameStateModel()
        sendToClient(Addressing.onGameStarted(), GameStartedMessage(id, gsm), clientID)
        service.playersExcept(id).forEach {
            sendToClient(Addressing.onEnemyConnected(), EnemyConnectedMessage(id), it)
        }
    }

    private fun throwBrickHandler(msg: Message<ThrowBrickMessage>) {
        val clientID = clientFromHeaders(msg) ?: return
        val body = msg.body() ?: return
        service.onThrowBrick(body, clientID)
    }

    private fun moveHandler(msg: Message<MoveMessage>) {
        val clientID = clientFromHeaders(msg) ?: return
        val body = msg.body() ?: return
        service.onMove(body, clientID)
    }

    private fun tickHandler(msg: Message<Long>) {
        val delta = msg.body() ?: return
        val states = service.onTick(delta)
        states.forEach { state ->
            val gmm = GameStateMessage(state.second)
            sendToClient(Addressing.onGameState(), gmm, state.first)
        }
    }

    private fun disconnectHandler(msg: Message<String>) {
        logger.info("Room-$roomID onDisconnect: ${msg.body()}")
        val uuid: String? = msg.body()
        if (uuid != null) {
            val clientID = UUID.fromString(uuid)
            service.onDisconnect(clientID)
            service.playersExcept(clientID).forEach {
                sendToClient(Addressing.onEnemyDisconnected(), EnemyDisconnectedMessage(clientID), it)
            }
        }
        if (service.shouldStop()) {
            vertx.undeploy(deploymentID())
        }
    }

    private fun <T: IMessage> clientFromHeaders(msg: Message<T>): UUID? {
        val id = msg.headers().get(headerClientID) ?: return null
        return UUID.fromString(id)
    }

    private fun <T: IMessage> sendToClient(address: String, msg: T, clientID: UUID) {
        val options = DeliveryOptions()
        options.addHeader(headerClientID, clientID.toString())
        vertx.eventBus().publish(address, msg, options)
    }
}
