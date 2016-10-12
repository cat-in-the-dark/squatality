package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class BonusComponent(
    var typeName: String? = null
): Component, Pool.Poolable {
    override fun reset() {
        typeName = null
    }
}
