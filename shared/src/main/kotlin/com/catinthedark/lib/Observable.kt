package com.catinthedark.lib

import java.util.*

class Observable<T> {
    private val observers: MutableMap<UUID, (T) -> Unit> = hashMapOf()

    fun subscribe(observer: (T) -> Unit): UUID {
        val id = UUID.randomUUID()
        observers.put(id, observer)
        return id
    }

    fun unsubscribe(id: UUID?) {
        observers.remove(id)
    }

    operator fun invoke(data: T) {
        observers.values.forEach {
            it.invoke(data)
        }
    }

    fun clear() {
        observers.clear()
    }
}
