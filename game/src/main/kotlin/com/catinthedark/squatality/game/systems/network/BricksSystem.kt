package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.network.NetworkComponent
import com.catinthedark.squatality.models.BrickModel

class BricksSystem : NetworkSystem<BrickModel>(
    Family.all().get()
) {
    override fun getNetworkComponent(entity: Entity): NetworkComponent<BrickModel>? {
        return Mappers.network.bricks[entity]
    }

    override fun process(entity: Entity, model: Pair<List<BrickModel>, Long>, deltaTime:Float) {

    }
}
