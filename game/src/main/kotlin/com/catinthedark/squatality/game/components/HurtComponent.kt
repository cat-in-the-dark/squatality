package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class HurtComponent(
    var hurting: Boolean = false
): Component, Pool.Poolable {
    override fun reset() {
        hurting = false
    }
}
