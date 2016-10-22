package com.catinthedark.lib.invoker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

/**
 * It's queue of method calls.
 */
class InvokeQueue : Runnable {
    private val queue: BlockingQueue<() -> Any?> = LinkedBlockingDeque()
    private var shouldStop: Boolean = false
    val LOG: Logger = LoggerFactory.getLogger(InvokeQueue::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <T> put(call: () -> T?) {
        queue.put(call)
    }

    fun stop() {
        shouldStop = true
    }

    override fun run() {
        try {
            while (!shouldStop) {
                consume(queue.take())
            }
            LOG.info("Gracefully exit ${Thread.currentThread().id}. Has ${queue.size} tasks unfinished.")
        } catch (e: InterruptedException) {
            LOG.info("Force exit ${Thread.currentThread().id}. Has ${queue.size} tasks unfinished.")
        } catch (e: Exception) {
            LOG.error("Unexpected error: ${e.message}", e)
        }
    }

    inline fun consume(call: () -> Any?) {
        try {
            call()
        } catch (e: Exception) {
            LOG.error("Call error: ${e.message}", e)
        }
    }
}
