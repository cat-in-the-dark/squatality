package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector3
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.World
import com.catinthedark.squatality.game.components.*
import com.catinthedark.squatality.game.components.network.NetworkComponent
import com.catinthedark.squatality.models.BrickModel

class BricksSystem(
    private val world: World
) : NetworkSystem<BrickModel>() {
    private val family = Family.all(
        LerpTransformComponent::class.java,
        TextureComponent::class.java,
        RemoteIDComponent::class.java,
        HurtComponent::class.java
    ).get()
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        entities = engine.getEntitiesFor(family)
    }

    override fun getNetworkComponent(entity: Entity): NetworkComponent<BrickModel>? {
        return Mappers.network.bricks[entity]
    }

    override fun process(model: Pair<List<BrickModel>, Long>, deltaTime: Float) {
        val (bm, delay) = model
        val ids = entities.map { Mappers.remote.id[it].id }
        bm.forEach { brick ->
            if (ids.none { it == brick.id }) {
                engine.addEntity(world.createBrick(brick))
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
        entities.forEach { processEntity(it, model, deltaTime) }
    }

    private fun processEntity(entity: Entity, model: Pair<List<BrickModel>, Long>, deltaTime: Float) {
        val (bm, delay) = model
        val ltc = Mappers.lerpTransform[entity] ?: return
        val hc = Mappers.hurt[entity] ?: return
        val rc = Mappers.remote.id[entity] ?: return
        val target = bm.find { it.id == rc.id } ?: return
        ltc.syncDelta = delay
        hc.hurting = target.hurting
        ltc.queue.add(LerpTransformElement(
            prevPos = Vector3(target.previousX, target.previousY, 0f),
            pos = Vector3(target.x, target.y, 0f),
            angle = target.angle.toFloat()
        ), delay(ltc.syncDelta))
    }

    private fun delay(delta: Long): Long {
        return if (delta > Const.Network.Server.tickDelay) {
            Const.Network.Server.tickDelay
        } else {
            delta
        }
    }
}
