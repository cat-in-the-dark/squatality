package com.catinthedark.lib

import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Use this Observable if you want to invoke callbacks in different threads than main thread.
 * My be useful to invoke some heavy code that isn't important.
 */
class ScheduledObservable<T>(
    val executor: Executor = Executors.newFixedThreadPool(4)
) : IObservable<T> {
    private val observers: MutableMap<UUID, (T) -> Unit> = hashMapOf()

    override fun subscribe(observer: (T) -> Unit): UUID {
        val id = UUID.randomUUID()
        observers.put(id, observer)
        return id
    }

    override fun unsubscribe(id: UUID?) {
        observers.remove(id)
    }

    override operator fun invoke(data: T) {
        observers.values.forEach {
            executor.execute {
                it.invoke(data)
            }
        }
    }

    override fun clear() {
        observers.clear()
    }
}
