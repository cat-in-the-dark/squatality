package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.TimeUtils
import com.catinthedark.lib.Observable
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.LerpTransformComponent
import com.catinthedark.squatality.game.components.LerpTransformElement
import com.catinthedark.squatality.game.components.RemoteIDComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.models.GameStateModel
import java.util.concurrent.ConcurrentLinkedQueue

class RemoteControlSystem(
    private val onGameState: Observable<GameStateModel>
) : IteratingSystem(
    Family.all(LerpTransformComponent::class.java, StateComponent::class.java, RemoteIDComponent::class.java).get()
) {
    private val gameStates = ConcurrentLinkedQueue<Pair<GameStateModel, Long>>()
    private var syncDelta = 0L
    private var lastSyncTime = -1L

    val getSyncDelta: () -> Long = { TimeUtils.nanosToMillis(syncDelta) }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        onGameState.subscribe { state ->
            if (lastSyncTime == -1L) lastSyncTime = System.nanoTime()
            val now = System.nanoTime()
            syncDelta = now - lastSyncTime
            lastSyncTime = now
            gameStates.add(Pair(state, getSyncDelta()))
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        while (gameStates.isNotEmpty()) {
            val (gs, delay) = gameStates.poll() ?: break
            entities.forEach { entity ->
                val sc = Mappers.state[entity] ?: return@forEach
                val rc = Mappers.remote.id[entity] ?: return@forEach
                val ltc = Mappers.lerpTransform[entity] ?: return@forEach
                val target = gs.players.find { it.id == rc.id } ?: return@forEach
                ltc.queue.add(LerpTransformElement(
                    pos = Vector3(target.x, target.y, 0f),
                    angle = target.angle
                ), delay)
                sc.state = target.state.name
            }
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        // nothing to do
    }
}
