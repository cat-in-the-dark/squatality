package com.catinthedark.lib

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SimpleExecutor: IExecutor {
    private val executor = Executors.newScheduledThreadPool(1)

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
