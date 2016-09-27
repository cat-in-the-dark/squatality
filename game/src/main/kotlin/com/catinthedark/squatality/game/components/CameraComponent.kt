package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class CameraComponent(
    val leftBottomCorner: Vector2 = Vector2(),
    val rightUpperCorner: Vector2 = Vector2()
): Component
