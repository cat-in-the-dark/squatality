package com.catinthedark.lib

import java.util.concurrent.TimeUnit

/**
 * Common interface for deffer, periodic actions.
 * It could be implemented based on
 * 1) Java scheduler with thread executors
 * 2) Single thread ticks
 * 3) Single thread event loop
 * 4) etc...
 */
interface IExecutor {
    fun deffer(delay: Long, timeUnit: TimeUnit, callback: () -> Unit)
    fun periodic(delay: Long, timeUnit: TimeUnit, callback: () -> Unit)
}
