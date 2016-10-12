package com.catinthedark.squatality.game.components.network

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.catinthedark.squatality.models.BonusModel
import java.util.*

data class BonusesComponent(
    val queue: Queue<Pair<List<BonusModel>, Long>> = LinkedList()
) : Component, Pool.Poolable {
    override fun reset() {
        queue.clear()
    }
}
