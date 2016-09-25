package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

data class TransformComponent(
    val pos: Vector3 = Vector3(),
    val scale: Vector2 = Vector2(1f, 1f),
    var angle: Float = 0f
): Component
