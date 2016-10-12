package com.catinthedark.squatality.game.components.network

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.catinthedark.squatality.models.BrickModel
import java.util.*

data class BricksComponent(
    val queue: Queue<Pair<List<BrickModel>, Long>> = LinkedList()
) : Component, Pool.Poolable {
    override fun reset() {
        queue.clear()
    }
}
