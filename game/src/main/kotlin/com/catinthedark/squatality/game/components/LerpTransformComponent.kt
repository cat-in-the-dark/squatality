package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.catinthedark.lib.collections.WeightedQueue

data class LerpTransformComponent(
    val queue: WeightedQueue<LerpTransformElement> = WeightedQueue(100)
) : Component, Pool.Poolable {
    override fun reset() {
        queue.clear()
    }
}

data class LerpTransformElement(
    val pos: Vector3 = Vector3(),
    var angle: Float = 0f
)
