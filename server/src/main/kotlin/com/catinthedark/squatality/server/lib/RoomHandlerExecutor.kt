package com.catinthedark.squatality.server.lib

import com.catinthedark.lib.IExecutor
import com.catinthedark.lib.invoker.InvokeWrapper
import com.catinthedark.squatality.server.RoomHandlers
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * This is special invoker to work in the certain wrapped room handler's thread.
 */
class RoomHandlerExecutor(
    private val invoker: InvokeWrapper<RoomHandlers>?,
    private val executor: ScheduledExecutorService
) : IExecutor {
    private val LOG = LoggerFactory.getLogger(RoomHandlerExecutor::class.java)

    override fun deffer(delay: Long, timeUnit: TimeUnit, callback: () -> Unit) {
        executor.schedule({
            invoker?.invoke(RoomHandlers::onFunc, callback) ?: LOG.warn("Invoker is null. Strange.")
        }, delay, timeUnit)
    }

    override fun periodic(delay: Long, timeUnit: TimeUnit, callback: () -> Unit) {
        executor.scheduleWithFixedDelay({
            invoker?.invoke(RoomHandlers::onFunc, callback) ?: LOG.warn("Invoker is null. Strange.")
        }, delay, delay, timeUnit)
    }

    fun shutdown() {
        executor.shutdown()
    }
}
