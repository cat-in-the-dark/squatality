package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class StateComponent(
    var time: Float = 0f,
    var state: String? = null
): Component, Pool.Poolable {
    override fun reset() {
        time = 0f
        state = null
    }
}
