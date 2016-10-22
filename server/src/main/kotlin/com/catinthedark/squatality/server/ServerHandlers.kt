package com.catinthedark.squatality.server

import com.catinthedark.squatality.models.EnemyConnectedMessage
import com.catinthedark.squatality.models.EnemyDisconnectedMessage
import com.catinthedark.squatality.models.GameStartedMessage
import com.catinthedark.squatality.models.GameStateMessage
import java.util.*

/**
 * This events should be called from RoomService.
 * We need this to make RoomService unaware of transport system.
 */
interface ServerHandlers {
    fun emit(msg: GameStartedMessage, clientID: UUID)
    fun emit(msg: GameStateMessage, clientID: UUID)
    fun emit(msg: EnemyConnectedMessage, clientID: UUID)
    fun emit(msg: EnemyDisconnectedMessage, clientID: UUID)
}
