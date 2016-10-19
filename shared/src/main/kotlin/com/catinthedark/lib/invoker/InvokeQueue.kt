package com.catinthedark.lib.invoker

import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque

/**
 * It's queue of method calls.
 */
class InvokeQueue : Runnable {
    private val queue: BlockingQueue<Pair<() -> Any?, (Any?) -> Unit>> = LinkedBlockingDeque()
    private var shouldStop: Boolean = false
    private val LOG = LoggerFactory.getLogger(InvokeQueue::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <T> put(call: Pair<() -> T?, (T?) -> Unit>) {
        queue.put(call as Pair<() -> Any?, (Any?) -> Unit>)
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

    private fun consume(call: Pair<() -> Any?, (Any?) -> Unit>) {
        try {
            val res = call.first()
            call.second(res)
        } catch (e: Exception) {
            LOG.error("Call error: ${e.message}", e)
        }
    }
}
