package com.catinthedark.squatality.game.components.network

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import java.util.*

interface NetworkComponent<T> : Component, Pool.Poolable {
    val queue: Queue<Pair<List<T>, Long>>
    override fun reset() {
        queue.clear()
    }
}
