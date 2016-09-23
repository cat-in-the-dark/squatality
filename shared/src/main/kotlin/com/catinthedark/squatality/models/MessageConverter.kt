package com.catinthedark.squatality.models

import com.catinthedark.lib.JsonConverter
import com.catinthedark.lib.Parser
import com.catinthedark.lib.Register
import com.catinthedark.squatality.models.*

object MessageConverter {
    private val converter = JsonConverter()

    init {
        registerMessages(converter)
    }

    val parser: Parser
        get() = converter

    private fun registerMessages(register: Register) {
        register.addAll(listOf(
            EnemyDisconnectedMessage::class.java,
            GameStartedMessage::class.java,
            RoundEndsMessage::class.java,
            HelloMessage::class.java,
            ServerHelloMessage::class.java,
            MoveMessage::class.java,
            GameStateMessage::class.java,
            SoundMessage::class.java,
            ThrowBrickMessage::class.java
        ))
    }
}
