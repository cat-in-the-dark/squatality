package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import com.catinthedark.lib.collections.WeightedQueue
import com.catinthedark.squatality.game.Const

data class LerpTransformComponent(
    val queue: WeightedQueue<LerpTransformElement> = WeightedQueue(Const.Network.lerpDelay),
    var syncDelta: Long = 0L
) : Component, Pool.Poolable {
    override fun reset() {
        queue.clear()
    }
}

data class LerpTransformElement(
    val prevPos: Vector3 = Vector3(),
    val pos: Vector3 = Vector3(),
    var angle: Float = 0f
)
