package com.catinthedark.lib.invoker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

/**
 * It's queue of method calls.
 */
class InvokeQueue : Runnable {
    private val queue: BlockingQueue<Pair<() -> Any?, (Any?) -> Unit>> = LinkedBlockingDeque()
    private var shouldStop: Boolean = false
    val LOG: Logger = LoggerFactory.getLogger(InvokeQueue::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <T> put(call: () -> T?, callback: (T?) -> Unit) {
        queue.put(Pair(call as () -> Any?, callback as (Any?) -> Unit))
    }

    fun stop() {
        shouldStop = true
    }

    override fun run() {
        try {
            while (!shouldStop) {
                val task = queue.take()
                consume(task.first, task.second)
            }
            LOG.info("Gracefully exit ${Thread.currentThread().id}. Has ${queue.size} tasks unfinished.")
        } catch (e: InterruptedException) {
            LOG.info("Force exit ${Thread.currentThread().id}. Has ${queue.size} tasks unfinished.")
        } catch (e: Exception) {
            LOG.error("Unexpected error: ${e.message}", e)
        }
    }

    inline fun consume(call: () -> Any?, callback: (Any?) -> Unit) {
        try {
            callback(call())
        } catch (e: Exception) {
            LOG.error("Call error: ${e.message}", e)
        }
    }
}
