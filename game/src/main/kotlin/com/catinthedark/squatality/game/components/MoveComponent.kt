package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

data class MoveComponent(
    val acceleration: Vector2 = Vector2(),
    val velocity: Vector2 = Vector2(),
    val prevVelocity: Vector2 = Vector2()
): Component, Pool.Poolable {
    override fun reset() {
        acceleration.setZero()
        velocity.setZero()
        prevVelocity.setZero()
    }
}
