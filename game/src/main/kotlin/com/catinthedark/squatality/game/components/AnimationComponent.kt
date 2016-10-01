package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.utils.Pool

data class AnimationComponent(
    val animations: MutableMap<String, Animation> = hashMapOf()
): Component, Pool.Poolable {
    override fun reset() {
        animations.clear()
    }
}
