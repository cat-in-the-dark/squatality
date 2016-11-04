package com.catinthedark.lib

class Observable<T> {
    private val observers: MutableList<(T) -> Unit> = arrayListOf()

    fun subscribe(observer: (T) -> Unit) {
        observers += observer
    }

    operator fun invoke(data: T) {
        observers.forEach {
            it.invoke(data)
        }
    }

    fun clear() {
        observers.clear()
    }
}
