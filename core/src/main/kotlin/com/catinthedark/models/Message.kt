package com.catinthedark.models

import com.catinthedark.lib.IMessage
import java.util.*

data class EnemyDisconnectedMessage(val clientId: UUID) : IMessage
data class GameStartedMessage(val clientId: UUID) : IMessage
data class RoundEndsMessage(val gameStateModel: GameStateModel) : IMessage
data class HelloMessage(val name: String) : IMessage
data class ServerHelloMessage(val clientId: UUID) : IMessage
data class MoveMessage(val speedX: Float, val speedY: Float, val angle: Float, val stateName: String) : IMessage
data class GameStateMessage(val gameStateModel: GameStateModel) : IMessage
data class SoundMessage(val soundName: SoundName) : IMessage
data class ThrowBrickMessage(val x: Float, val y: Float, val force: Float, val angle: Double) : IMessage
