package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

data class TransformComponent(
    val pos: Vector3 = Vector3(),
    var angle: Float = 0f
): Component, Pool.Poolable {
    override fun reset() {
        pos.setZero()
        angle = 0f
    }
}
