package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.IMessage
import com.catinthedark.lib.SimpleExecutor
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.models.*
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * This class is a central handler for all room-specific messages coming from clients.
 * In thread safe InvokeWrapper we trust.
 *
 * Every room has UUID id to subscribe on dynamically created message handlers.
 * @see com.catinthedark.lib.invoker.InvokeWrapper
 */
class RoomHandlersImpl(
    val roomID: UUID,
    val roomRegister: RoomRegister,
    private val publish: (IMessage, UUID) -> Unit,
    private val executor: IExecutor = SimpleExecutor()
) : RoomHandlers {
    private val LOG = LoggerFactory.getLogger(RoomHandlersImpl::class.java)!!
    private val service: RoomService = RoomService(executor, Const.Balance.maxPlayersInRoom)

    init {
        executor.periodic(Const.Balance.bonusDelay, TimeUnit.SECONDS, {
            roomRegister[roomID]?.invoke(RoomHandlers::onSpawnBonus)
        })
    }

    override fun onMove(msg: MoveMessage, clientID: UUID) {
        service.onMove(msg, clientID)
    }

    override fun onHello(msg: HelloMessage, clientID: UUID): UUID? {
        val id = service.onNewClient(msg, clientID) ?: return null
        val gsm = service.buildGameStateModel()
        publish(GameStartedMessage(id, gsm), clientID)
        service.playersExcept(id).forEach {
            publish(EnemyConnectedMessage(id), it)
        }
        return id
    }

    override fun onThrowBrick(msg: ThrowBrickMessage, clientID: UUID) {
        service.onThrowBrick(msg, clientID)
    }

    override fun onDisconnect(clientID: UUID) {
        LOG.info("RoomHandlers-$roomID onDisconnect: $clientID")
        service.onDisconnect(clientID)
        service.playersExcept(clientID).forEach {
            publish(EnemyDisconnectedMessage(clientID), it)
        }
        if (service.shouldStop()) {
            roomRegister.unregister(roomID)
        }
    }

    override fun onTick(deltaTime: Long) {
        val states = service.onTick(deltaTime)
        states.forEach { state ->
            val gmm = GameStateMessage(state.second)
            publish(gmm, state.first)
        }
        while(service.output.isNotEmpty()) {
            val msg = service.output.poll()
            msg.to.forEach { publish(msg.body, it) }
        }
    }

    override fun onSpawnBonus() {
        service.onSpawnBonus()
    }
}
