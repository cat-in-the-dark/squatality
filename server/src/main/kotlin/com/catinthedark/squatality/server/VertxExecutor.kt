package com.catinthedark.squatality.server

import com.catinthedark.lib.IExecutor
import io.vertx.core.Vertx
import java.util.concurrent.TimeUnit

class VertxExecutor(
    private val vertx: Vertx
) : IExecutor {
    override fun periodic(delay: Long, timeUnit: TimeUnit, callback: () -> Unit) {
        vertx.setPeriodic(TimeUnit.MILLISECONDS.convert(delay, timeUnit), {
            callback()
        })
    }

    override fun deffer(delay: Long, timeUnit: TimeUnit, callback: () -> Unit) {
        vertx.setTimer(TimeUnit.MILLISECONDS.convert(delay, timeUnit), {
            callback()
        })
    }
}
