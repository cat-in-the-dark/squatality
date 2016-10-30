package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class StateComponent(
    var time: Float = 0f,
    var state: String? = null,
    var hasBrick: Boolean = false,
    var bonuses: List<String> = emptyList()
) : Component, Pool.Poolable {
    override fun reset() {
        time = 0f
        state = null
        hasBrick = false
    }

    fun animState(): String? {
        return if (hasBrick) {
            "${state}_WITH_BRICK"
        } else {
            state
        }
    }
}
