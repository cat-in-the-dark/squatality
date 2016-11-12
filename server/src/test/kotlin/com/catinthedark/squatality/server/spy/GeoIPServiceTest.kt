package com.catinthedark.squatality.server.spy

import org.junit.Assert
import org.junit.Test

class GeoIPServiceTest {
    val geo = GeoIPService()

    @Test
    fun Should_Find() {
        val res = geo.find("")
        val model = res.get()
        Assert.assertFalse(res.isCompletedExceptionally)
        Assert.assertNotNull(model)
        println(model)
    }
}
