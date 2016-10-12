package com.catinthedark.squatality.game.systems.network

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.lib.Observable
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.network.BonusesComponent
import com.catinthedark.squatality.game.components.network.BricksComponent
import com.catinthedark.squatality.game.components.network.PlayersComponent
import com.catinthedark.squatality.game.utils.TimeConverter
import com.catinthedark.squatality.models.GameStateModel
import java.util.concurrent.ConcurrentLinkedQueue

class RemoteSyncSystem(
    private val onGameState: Observable<GameStateModel>
) : IteratingSystem(
    Family.all(BonusesComponent::class.java, BricksComponent::class.java, PlayersComponent::class.java).get()
) {
    private val gameStates = ConcurrentLinkedQueue<Pair<GameStateModel, Long>>()
    private var syncDelta = 0L
    private var lastSyncTime = -1L
    private var currentTime = 0L

    val getSyncDelta: () -> Long = { syncDelta }

    private fun onSync() {
        if (lastSyncTime == -1L) lastSyncTime = currentTime
        val now = currentTime
        syncDelta = now - lastSyncTime
        lastSyncTime = now
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        onGameState.subscribe { state ->
            onSync()
            gameStates.add(Pair(state, syncDelta))
        }
    }

    override fun update(deltaTime: Float) {
        currentTime += TimeConverter.secondsToMillis(deltaTime)
        super.update(deltaTime)
        while (gameStates.isNotEmpty()) {
            val (gs, delay) = gameStates.poll() ?: break
            entities.forEach { entity ->
                val pc = Mappers.network.players[entity]
                val bc = Mappers.network.bonuses[entity]
                val brc = Mappers.network.bricks[entity]
                pc.queue.add(Pair(gs.players, delay))
                bc.queue.add(Pair(gs.bonuses, delay))
                brc.queue.add(Pair(gs.bricks, delay))
            }
        }
    }


    override fun processEntity(entity: Entity?, deltaTime: Float) {
        // nothing to do here
    }
}