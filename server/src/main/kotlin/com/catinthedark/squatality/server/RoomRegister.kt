package com.catinthedark.squatality.server

import com.catinthedark.lib.IMessage
import com.catinthedark.lib.invoker.InvokeService
import com.catinthedark.lib.invoker.InvokeWrapper
import com.catinthedark.squatality.server.lib.RoomHandlerExecutor
import com.catinthedark.squatality.server.spy.SpyService
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class RoomRegister(
    private val ger: ServerGameEventsRegistrar
) {
    private val LOG = LoggerFactory.getLogger(RoomRegister::class.java)
    private val map: MutableMap<UUID, InvokeWrapper<RoomHandlers>> = hashMapOf()
    private val invoker = InvokeService()

    fun unregister(id: UUID) {
        map.remove(id)
        LOG.info("RoomHandlers-$id stopped")
        LOG.info("Rooms online: ${map.size}")
    }

    fun register(id: UUID, publish: (IMessage, UUID) -> Unit, disconnect: (UUID) -> Unit, executor: ScheduledExecutorService) {
        val roomHandler = RoomHandlersImpl(id, {
            unregister(id)
        }, publish, disconnect, ger)
        val wrappedRoom = invoker.wrap(roomHandler)
        roomHandler.onCreated(RoomHandlerExecutor(wrappedRoom, executor))
        map[id] = wrappedRoom
        LOG.info("RoomHandlers-$id started")
    }

    operator fun get(id: UUID?): InvokeWrapper<RoomHandlers>? {
        if (id == null) return null
        return map[id]
    }

    fun forEach(function: (InvokeWrapper<RoomHandlers>) -> Unit) {
        map.values.forEach(function)
    }
}
