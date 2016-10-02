package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import java.util.*

data class RemoteIDComponent(
    var id: UUID = UUID.randomUUID()
): Component, Pool.Poolable {
    override fun reset() {
        // do nothing
    }
}
