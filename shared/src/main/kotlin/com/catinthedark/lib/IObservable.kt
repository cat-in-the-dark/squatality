package com.catinthedark.lib

import java.util.*

interface IObservable<T> {
    fun subscribe(observer: (T) -> Unit): UUID

    fun unsubscribe(id: UUID?)

    operator fun invoke(data: T)

    fun clear()
}
