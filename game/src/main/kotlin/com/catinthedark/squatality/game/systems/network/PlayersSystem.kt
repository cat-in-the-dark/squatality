package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.LerpTransformComponent
import com.catinthedark.squatality.game.components.LerpTransformElement
import com.catinthedark.squatality.game.components.RemoteIDComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.network.BonusesComponent
import com.catinthedark.squatality.game.components.network.BricksComponent
import com.catinthedark.squatality.game.components.network.PlayersComponent
import com.catinthedark.squatality.models.PlayerModel

class PlayersSystem: IteratingSystem(
    Family.all(LerpTransformComponent::class.java, StateComponent::class.java, RemoteIDComponent::class.java).get()
) {
    private var syncEntity: Entity? = null

    override fun addedToEngine(engine: Engine?) {
        val e = engine?.getEntitiesFor(Family.all(BonusesComponent::class.java, BricksComponent::class.java, PlayersComponent::class.java).get())
        syncEntity = e?.firstOrNull() ?: throw Exception("Sync Entity should be created")
        super.addedToEngine(engine)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val pc = Mappers.network.players[syncEntity] ?: return
        while (pc.queue.isNotEmpty()) {
            val data = pc.queue.poll() ?: break
            entities.forEach { process(it, data) }
        }
    }

    private fun process(entity: Entity?, model: Pair<List<PlayerModel>, Long>) {
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
        sc.state = target.state.name
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        // nothing to do
    }
}
