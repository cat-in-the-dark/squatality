package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component

data class StateComponent(
    var time: Float = 0f,
    var state: String? = null
): Component
