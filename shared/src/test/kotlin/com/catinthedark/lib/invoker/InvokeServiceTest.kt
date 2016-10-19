package com.catinthedark.lib.invoker

import org.junit.Assert.assertEquals
import org.junit.Test

class InvokeServiceTest {
    @Test
    fun Should_ReturnFuture() {
        val service = InvokeService()
        val h1 = service.wrap(Actor1())
        val h2 = service.wrap(Actor2())
        assertEquals(10L, h1(Actor1::handleLong, 1L).get())
        assertEquals("hello", h2(Actor2::handleStr, "HeLlO").get())
        service.shutdown()
    }

    class Actor1 {
        fun handleLong(data: Long): Long {
            return data * 10L
        }

        fun handleStr(data: String): String {
            return data.toUpperCase()
        }

        fun handle() {
            println("Some call")
        }
    }

    class Actor2 {
        fun handleLong(data: Long): Long {
            return data * 5L
        }

        fun handleStr(data: String): String {
            return data.toLowerCase()
        }
    }
}
