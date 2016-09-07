package com.catinthedark.squatality.server

import com.catinthedark.models.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageCodec

abstract class RoomMessageCodec<M> : MessageCodec<M, M>  {
    val mapper = ObjectMapper().registerKotlinModule()
    abstract val clazz: Class<M>

    override fun decodeFromWire(pos: Int, buffer: Buffer): M {
        val size = buffer.getInt(pos)
        val json = buffer.getString(pos + 4, pos + 4 + size) // because of Int
        return mapper.readValue(json, clazz)
    }

    override fun transform(msg: M): M {
        return msg
    }

    override fun encodeToWire(buffer: Buffer, msg: M) {
        val json: String = mapper.writeValueAsString(msg)
        buffer.appendInt(json.toByteArray().size)
        buffer.appendString(json)
    }

    override fun systemCodecID(): Byte = -1

    override fun name(): String = this.javaClass.canonicalName
}

class HelloMessageCodec(override val clazz: Class<HelloMessage> = HelloMessage::class.java) : RoomMessageCodec<HelloMessage>()
class MoveMessageCodec(override val clazz: Class<MoveMessage> = MoveMessage::class.java) : RoomMessageCodec<MoveMessage>()
class ThrowBrickMessageCodec(override val clazz: Class<ThrowBrickMessage> = ThrowBrickMessage::class.java) : RoomMessageCodec<ThrowBrickMessage>()
class GameStartedMessageCodec(override val clazz: Class<GameStartedMessage> = GameStartedMessage::class.java) : RoomMessageCodec<GameStartedMessage>()
class GameStateMessageCodec(override val clazz: Class<GameStateMessage> = GameStateMessage::class.java) : RoomMessageCodec<GameStateMessage>()

fun registerCodecs(bus: EventBus) {
    bus.registerDefaultCodec(HelloMessage::class.java, HelloMessageCodec())
    bus.registerDefaultCodec(MoveMessage::class.java, MoveMessageCodec())
    bus.registerDefaultCodec(ThrowBrickMessage::class.java, ThrowBrickMessageCodec())
    bus.registerDefaultCodec(GameStartedMessage::class.java, GameStartedMessageCodec())
    bus.registerDefaultCodec(GameStateMessage::class.java, GameStateMessageCodec())
}
