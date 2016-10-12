package com.catinthedark.squatality.game.components.network

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.catinthedark.squatality.models.PlayerModel
import java.util.*

data class PlayersComponent(
    val queue: Queue<Pair<List<PlayerModel>, Long>> = LinkedList()
) : Component, Pool.Poolable {
    override fun reset() {
        queue.clear()
    }
}
