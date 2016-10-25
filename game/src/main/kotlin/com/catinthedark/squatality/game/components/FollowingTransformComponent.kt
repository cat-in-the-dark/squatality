package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class FollowingTransformComponent(
    var target: TransformComponent? = null,
    var strategy: (TransformComponent?, TransformComponent) -> Unit = {from, to -> }
): Component, Pool.Poolable {
    override fun reset() {
        target = null
    }
}
