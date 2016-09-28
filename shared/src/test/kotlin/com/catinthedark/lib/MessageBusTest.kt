package com.catinthedark.lib

import org.junit.Test

class MessageBusTest {
    class A: IMessage {
        fun a() = "A"
    }

    class B: IMessage {
        fun b() = "B"
    }

    @Test
    fun Should_Deliver() {
        val transport: Transport = LocalTransport()
        val bus = MessageBus(transport)
        bus.subscribe(A::class.java, {
            println(it.a())
        })
        bus.subscribe(B::class.java, {
            println(it.b())
        })

        bus.send(A())
        bus.send(B())
    }
}
