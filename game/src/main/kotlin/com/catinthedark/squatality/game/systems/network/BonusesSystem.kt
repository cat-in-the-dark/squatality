package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.World
import com.catinthedark.squatality.game.components.BonusComponent
import com.catinthedark.squatality.game.components.RemoteIDComponent
import com.catinthedark.squatality.game.components.TextureComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.components.network.NetworkComponent
import com.catinthedark.squatality.models.BonusModel

class BonusesSystem(
    private val world: World
) : NetworkSystem<BonusModel>() {
    private val family = Family.all(
        TransformComponent::class.java,
        TextureComponent::class.java,
        RemoteIDComponent::class.java,
        BonusComponent::class.java
    ).get()
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        entities = engine.getEntitiesFor(family)
    }

    override fun getNetworkComponent(entity: Entity): NetworkComponent<BonusModel>? {
        return Mappers.network.bonuses[entity]
    }

    override fun process(model: Pair<List<BonusModel>, Long>, deltaTime: Float) {
        val (bm, delay) = model
        val ids = entities.map { Mappers.remote.id[it].id }
        bm.forEach { bonus ->
            if (ids.none { it == bonus.id }) {
                engine.addEntity(world.createBonus(bonus))
            }
        }
        ids.forEach { id ->
            if (bm.none { it.id == id }) {
                entities.filter {
                    Mappers.remote.id[it].id == id
                }.forEach {
                    engine.removeEntity(it)
                }
            }
        }
    }
}
