package com.catinthedark.squatality.models

import com.catinthedark.lib.kryo.UUIDSerializer
import com.esotericsoftware.kryo.Kryo
import java.util.*

object MessageConverter {
    val kryoRegister: (Kryo) -> Unit = {
        it.apply {
            register(UUID::class.java, UUIDSerializer())
            register(ArrayList::class.java)
            register(EnemyConnectedMessage::class.java)
            register(EnemyDisconnectedMessage::class.java)
            register(GameStartedMessage::class.java)
            register(RoundEndsMessage::class.java)
            register(HelloMessage::class.java)
            register(ServerHelloMessage::class.java)
            register(MoveMessage::class.java)
            register(GameStateMessage::class.java)
            register(ThrowBrickMessage::class.java)
            register(GameStateModel::class.java)
            register(KillMessage::class.java)
            register(BonusModel::class.java)
            register(BrickModel::class.java)
            register(PlayerModel::class.java)
            register(State::class.java)
        }
    }
}
