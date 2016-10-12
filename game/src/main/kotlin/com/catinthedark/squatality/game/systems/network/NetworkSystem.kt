package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.catinthedark.squatality.game.components.network.BonusesComponent
import com.catinthedark.squatality.game.components.network.BricksComponent
import com.catinthedark.squatality.game.components.network.NetworkComponent
import com.catinthedark.squatality.game.components.network.PlayersComponent

abstract class NetworkSystem<T>(
    priority: Int = 0
) : EntitySystem(priority) {
    private val syncFamily = Family.all(
        BonusesComponent::class.java,
        BricksComponent::class.java,
        PlayersComponent::class.java
    ).get()
    private lateinit var syncEntity: Entity
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        syncEntity = engine
            .getEntitiesFor(syncFamily)
            .firstOrNull() ?: throw Exception("Sync Entity should be created")
        super.addedToEngine(engine)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val pc = getNetworkComponent(syncEntity) ?: return
        while (pc.queue.isNotEmpty()) {
            val model = pc.queue.poll() ?: break
            process(model, deltaTime)
        }
    }

    abstract fun getNetworkComponent(entity: Entity): NetworkComponent<T>?
    abstract fun process(model: Pair<List<T>, Long>, deltaTime: Float)
}
