package com.catinthedark.lib

import org.junit.Test

class RouteMachineTest {
    class A: YieldUnit<Int, String> {
        var data: Int = 0

        override fun onActivate(data: Int) {
            println("A.onActivate($data)")
            this.data = data
        }

        override fun run(delta: Float): String {
            return data.toString()
        }

        override fun onExit() {
            println("A.onExit#$data")
        }
    }

    class B: YieldUnit<String, Int> {
        var data: Int = 0
        var incremented = false

        override fun onActivate(data: String) {
            println("B.onActivate($data)")
            this.data = data.toInt()
            this.incremented = false
        }

        override fun run(delta: Float): Int? {
            if (incremented) {
                return data
            } else {
                incremented = true
                data++
            }
            return null
        }

        override fun onExit() {
            println("B.onExit")
        }
    }

    @Test
    fun Should_Route() {
        val a = A()
        val b = B()
        val rm = RouteMachine()
        rm.addRoute(a, { b })
        rm.addRoute(b, { a })
        rm.start(a, 0)
        0.until(10).forEach { rm.run(0f) }
    }
}
