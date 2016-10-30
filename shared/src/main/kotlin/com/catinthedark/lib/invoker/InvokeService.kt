package com.catinthedark.lib.invoker

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Special service to make calls to functions of some service thread safe.
 * Each call will be scheduled in queue.
 * It means that every wrapped class will be run in a single thread, but InvokeService allocate thread pool.
 */
class InvokeService(val numThreads: Int = 4) {
    val executor: ExecutorService = Executors.newFixedThreadPool(numThreads)
    private val queues = 0.until(numThreads).map { InvokeQueue() }
    private var lastIndex = 0

    init {
        queues.forEach { executor.submit(it) }
        executor.shutdown()
    }

    fun <T> wrap(target: T): InvokeWrapper<T> {
        lastIndex = (lastIndex + 1) % queues.size
        return InvokeWrapper(target, Invoker(queues[lastIndex]))
    }

    fun shutdown() {
        queues.forEach { it.stop() }
        executor.shutdownNow()
    }
}
