package com.catinthedark.lib.collections

import org.junit.Assert.assertEquals
import org.junit.Test

class WeightedQueueTest {
    @Test
    fun Should_PollParts() {
        val queue = WeightedQueue<String>()
        queue.add("a", 3)
        queue.add("b", 2)
        queue.add("c", 4)
        queue.add("d", 5)
        queue.add("e", 2)
        assertEquals(16, queue.weight())
        assertEquals("a", queue.poll(1).last().payload)
        assertEquals(15, queue.weight())
        assertEquals("b", queue.poll(3).last().payload)
        assertEquals(12, queue.weight())
        assertEquals("c", queue.poll(2).last().payload)
        assertEquals(10, queue.weight())
        assertEquals("e", queue.poll(100).last().payload)
        assertEquals(0, queue.weight())
        assertEquals(0, queue.size)
    }
}
