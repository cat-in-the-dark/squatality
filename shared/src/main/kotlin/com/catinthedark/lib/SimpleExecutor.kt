package com.catinthedark.lib

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SimpleExecutor(
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
): IExecutor {
    override fun deffer(delay: Long, timeUnit: TimeUnit, callback: () -> Unit) {
        executor.schedule(callback, delay, timeUnit)
    }

    override fun periodic(delay: Long, timeUnit: TimeUnit, callback: () -> Unit) {
        executor.scheduleWithFixedDelay(callback, delay, delay, timeUnit)
    }

    fun shutdown() {
        executor.shutdown()
    }
}
