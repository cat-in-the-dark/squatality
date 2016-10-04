package com.catinthedark.squatality.game.utils

object TimeConverter {
    fun secondsToMillis(seconds: Float): Long {
        return (seconds * 1000).toLong()
    }
}
