package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera

data class CameraComponent(
    val target: Entity? = null,
    val camera: OrthographicCamera? = null
): Component
