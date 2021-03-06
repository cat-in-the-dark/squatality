package com.catinthedark.lib

import java.util.*

class Observable<T> : IObservable<T> {
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
            it.invoke(data)
        }
    }

    override fun clear() {
        observers.clear()
    }
}
