package com.catinthedark.lib

import org.junit.Assert
import org.junit.Test

class JsonConverterTest {
    data class SomeClass(val a: String, val b: Long): IMessage
    data class AnotherClass(val c: Int, val d: String): IMessage

    val jsonConverter = JsonConverter()

    init {
        jsonConverter.addAll(listOf(SomeClass::class.java, AnotherClass::class.java))
    }

    @Test
    fun Should_Wrap() {
        val s = SomeClass("test", 1)
        val json = jsonConverter.wrap(s)
        val data = jsonConverter.unwrap(json)
        Assert.assertEquals(s.javaClass.canonicalName, data.javaClass.canonicalName)
        if (data is SomeClass) {
            Assert.assertEquals(s.a, data.a)
            Assert.assertEquals(s.b, data.b)
        } else {
            Assert.fail("Data Should be instance of SomeClass")
        }
    }
}
