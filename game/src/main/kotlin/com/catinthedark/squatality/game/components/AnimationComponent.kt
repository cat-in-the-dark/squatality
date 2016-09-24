package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation

data class AnimationComponent(
    val animations: MutableMap<String, Animation> = hashMapOf()
): Component
