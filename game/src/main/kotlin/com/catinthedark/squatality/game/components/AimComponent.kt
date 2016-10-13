package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class AimComponent(
    var angle: Float = 0f,
    var force: Float = 0f,
    var aiming: Boolean = false
) : Component, Pool.Poolable {
    override fun reset() {
        angle = 0f
        force = 0f
        aiming = false
    }
}
