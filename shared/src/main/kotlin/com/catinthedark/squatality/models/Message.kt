package com.catinthedark.squatality.models

import com.catinthedark.lib.IMessage
import java.util.*

/**
 * If you change signature or behaviour of any IMessage or Model, increment this field.
 */
const val VERSION: Int = 1

/**
 * Avoid changing of this class, because it uses to handle API version and must always be read from the client.
 */
data class ServerHelloMessage(val clientId: UUID = UUID.randomUUID(), val version: Int = VERSION) : IMessage

data class EnemyDisconnectedMessage(val clientId: UUID = UUID.randomUUID()) : IMessage
data class EnemyConnectedMessage(val clientId: UUID = UUID.randomUUID()) : IMessage
data class GameStartedMessage(val clientId: UUID = UUID.randomUUID(), val gameStateModel: GameStateModel = GameStateModel()) : IMessage
data class RoundEndsMessage(val statistics: RoomStatisticsModel = RoomStatisticsModel()) : IMessage
data class HelloMessage(val name: String = "") : IMessage
data class MoveMessage(val speedX: Float = 0f, val speedY: Float = 0f, val angle: Float = 0f, val stateName: String = "") : IMessage
data class GameStateMessage(val gameStateModel: GameStateModel = GameStateModel()) : IMessage
data class ThrowBrickMessage(val x: Float = 0f, val y: Float = 0f, val force: Float = 0f, val angle: Double = 0.0) : IMessage
data class KillMessage(val victimId: UUID = UUID.randomUUID(), val killerIds: List<UUID> = emptyList(), val victimName: String = "", val killerNames: List<String> = emptyList()) : IMessage
