package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.lib.Observable
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.RemoteIDComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.models.GameStateModel
import java.util.concurrent.ConcurrentLinkedQueue

class RemoteControlSystem(
    private val onGameState: Observable<GameStateModel>
): IteratingSystem(
    Family.all(TransformComponent::class.java, StateComponent::class.java, RemoteIDComponent::class.java).get()
) {
    val states = ConcurrentLinkedQueue<GameStateModel>()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        onGameState.subscribe { state ->
            states.add(state)
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        val state = states.poll() ?: return
        entities.forEach { entity ->
            val rc = Mappers.remote.id[entity] ?: return@forEach
            val tc = Mappers.transform[entity] ?: return@forEach
            val target = state.players.find { it.id == rc.id } ?: return@forEach
            tc.pos.x = target.x
            tc.pos.y = target.y
            tc.angle = target.angle
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        // nothing to do
    }
}
