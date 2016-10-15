package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class ClockComponent(
    var time: Long = 0
): Component, Pool.Poolable {
    override fun reset() {
        time = 0
    }
}
