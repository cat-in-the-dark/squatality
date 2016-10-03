package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
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
) : IteratingSystem(
    Family.all(TransformComponent::class.java, StateComponent::class.java, RemoteIDComponent::class.java).get()
) {
    private val gameStates = ConcurrentLinkedQueue<GameStateModel>()
    private var syncDelta = 0L
    private var lastSyncTime = System.nanoTime()

    val getSyncDelta: () -> Long = { syncDelta / 1000000 }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        onGameState.subscribe { state ->
            val now = System.nanoTime()
            syncDelta = now - lastSyncTime
            lastSyncTime = now
            gameStates.add(state)
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        while (gameStates.isNotEmpty()) {
            val gs = gameStates.poll() ?: break
            entities.forEach { entity ->
                val sc = Mappers.state[entity] ?: return@forEach
                val rc = Mappers.remote.id[entity] ?: return@forEach
                val tc = Mappers.transform[entity] ?: return@forEach
                val target = gs.players.find { it.id == rc.id } ?: return@forEach
                tc.pos.x = target.x
                tc.pos.y = target.y
                tc.angle = target.angle
                sc.state = target.state.name
            }
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        // nothing to do
    }
}
