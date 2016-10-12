package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector3
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.LerpTransformComponent
import com.catinthedark.squatality.game.components.LerpTransformElement
import com.catinthedark.squatality.game.components.RemoteIDComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.network.NetworkComponent
import com.catinthedark.squatality.models.PlayerModel

class PlayersSystem : NetworkSystem<PlayerModel>() {
    private val family = Family.all(LerpTransformComponent::class.java, StateComponent::class.java, RemoteIDComponent::class.java).get()
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        entities = engine.getEntitiesFor(family)
    }

    override fun getNetworkComponent(entity: Entity): NetworkComponent<PlayerModel>? {
        return Mappers.network.players[entity]
    }

    override fun process(model: Pair<List<PlayerModel>, Long>, deltaTime: Float) {
        entities.forEach { process(it, model, deltaTime) }
    }

    fun process(entity: Entity, model: Pair<List<PlayerModel>, Long>, deltaTime: Float) {
        val (pm, delay) = model
        val sc = Mappers.state[entity] ?: return
        val rc = Mappers.remote.id[entity] ?: return
        val ltc = Mappers.lerpTransform[entity] ?: return
        val target = pm.find { it.id == rc.id } ?: return
        if (target.updated) {
            ltc.queue.add(LerpTransformElement(
                prevPos = Vector3(target.previousX, target.previousY, 0f),
                pos = Vector3(target.x, target.y, 0f),
                angle = target.angle
            ), ltc.syncDelta)
            ltc.syncDelta = 0L
        }
        ltc.syncDelta += delay
        if (target.hasBrick) {
            sc.state = "${target.state.name}_WITH_BRICK"
        } else {
            sc.state = target.state.name
        }
    }
}
