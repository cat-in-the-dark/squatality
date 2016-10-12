package com.catinthedark.lib

import org.junit.Test
import org.mockito.Mockito.*

class ObservableTest {
    open class Func {
        operator open fun invoke(data: String) {
            println(data)
        }
    }

    @Test
    fun Should_ObserverMany() {
        val observable = Observable<String>()
        val f = mock(Func::class.java)
        observable.subscribe { f(it) }
        observable.subscribe { f(it) }
        observable.invoke("data")
        observable.invoke("other")
        verify(f, times(2)).invoke("data")
        verify(f, times(2)).invoke("other")
    }
}
