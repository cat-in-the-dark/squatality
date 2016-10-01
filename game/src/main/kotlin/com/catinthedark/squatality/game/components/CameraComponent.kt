package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

data class CameraComponent(
    val leftBottomCorner: Vector2 = Vector2(),
    val rightUpperCorner: Vector2 = Vector2()
): Component, Pool.Poolable {
    override fun reset() {
        leftBottomCorner.setZero()
        rightUpperCorner.setZero()
    }
}
