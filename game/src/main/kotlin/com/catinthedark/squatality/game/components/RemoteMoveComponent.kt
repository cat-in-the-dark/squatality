package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

data class RemoteMoveComponent(
    val velocity: Vector3 = Vector3(),
    var angle: Float = 0f,
    var lastSync: Float = 0f
): Component, Pool.Poolable {
    override fun reset() {
        velocity.setZero()
        angle = 0f
        lastSync = 0f
    }
}
