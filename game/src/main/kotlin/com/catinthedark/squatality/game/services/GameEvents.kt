package com.catinthedark.squatality.game.services

import com.badlogic.gdx.utils.Disposable
import com.catinthedark.lib.Observable

interface GameEvent // marker interface

// TODO: fill this messages with some data
class ThrowBrickEvent : GameEvent
class EnemyConnectedEvent : GameEvent
class EnemyDisconnectedEvent : GameEvent
class KilledEvent : GameEvent
class RoundEndedEvent : GameEvent
class RoundStartedEvent : GameEvent
class BonusSpawnedEvent : GameEvent
class DisconnectedEvent : GameEvent

/**
 * This registrar is supposed to be used for sound systems, notifications.
 * I recommend to create it in the very beginning of the game and do not recreate this object.
 * Take a look - do not forget to UNSUBSCRIBE from events, otherwise you'll get funny bags.
 */
class GameEventsRegistrar : Disposable {
    val onThrowBrickEvent = Observable<ThrowBrickEvent>()
    val onEnemyConnectedEvent = Observable<EnemyConnectedEvent>()
    val onEnemyDisconnectedEvent = Observable<EnemyDisconnectedEvent>()
    val onKilledEvent = Observable<KilledEvent>()
    val onRoundStartedEvent = Observable<RoundStartedEvent>()
    val onRoundEndedEvent = Observable<RoundEndedEvent>()
    val onBonusSpawnedEvent = Observable<BonusSpawnedEvent>()
    val onDisconnectedEvent = Observable<DisconnectedEvent>()

    override fun dispose() {
        onThrowBrickEvent.clear()
        onEnemyConnectedEvent.clear()
        onEnemyDisconnectedEvent.clear()
        onKilledEvent.clear()
        onRoundStartedEvent.clear()
        onRoundEndedEvent.clear()
        onBonusSpawnedEvent.clear()
        onDisconnectedEvent.clear()
    }
}
