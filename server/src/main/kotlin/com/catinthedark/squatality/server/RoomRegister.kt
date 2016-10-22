package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.invoker.InvokeService
import com.catinthedark.lib.invoker.InvokeWrapper
import org.slf4j.LoggerFactory
import java.util.*

class RoomRegister {
    private val LOG = LoggerFactory.getLogger(RoomRegister::class.java)
    private val map: MutableMap<UUID, InvokeWrapper<RoomHandlers>> = hashMapOf()
    private val invoker = InvokeService()

    fun unregister(id: UUID) {
        map.remove(id)
        LOG.info("RoomHandlers-$id stopped")
        LOG.info("Rooms online: ${map.size}")
    }

    fun register(id: UUID, handlers: ServerHandlers, executor: IExecutor) {
        map[id] = invoker.wrap(RoomHandlersImpl(id, this, handlers, executor))
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
