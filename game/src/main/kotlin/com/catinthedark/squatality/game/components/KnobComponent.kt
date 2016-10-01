package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.utils.Pool

data class KnobComponent(
    var touchPad: Touchpad? = null
): Component, Pool.Poolable {
    override fun reset() {
        touchPad = null
    }
}
