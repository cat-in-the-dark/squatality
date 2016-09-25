package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad

data class KnobComponent(
    var touchPad: Touchpad? = null
): Component
