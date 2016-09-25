package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

data class MoveComponent(
    val acceleration: Vector2 = Vector2(),
    val velocity: Vector2 = Vector2()
): Component
