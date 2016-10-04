package com.catinthedark.squatality.server

import com.catinthedark.lib.IMessage
import com.catinthedark.squatality.models.*
import com.google.gson.Gson
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageCodec

/**
 * I apologize for all this stuff.
 * We decided to use statically typed messages in vertex eventBus
 * so we have to declare all this dumb codecs
 *
 * About out boundaries. I specify that this codec works only and only with IMessage.
 * This is the way of avoiding strange errors.
 */
class RoomMessageCodec<M : IMessage>(
    val clazz: Class<M>
) : MessageCodec<M, M> {
    val mapper = Gson()

    override fun decodeFromWire(pos: Int, buffer: Buffer): M {
        val size = buffer.getInt(pos)
        val json = buffer.getString(pos + 4, pos + 4 + size) // because of Int
        return mapper.fromJson(json, clazz)
    }

    override fun transform(msg: M): M {
        return msg
    }

    override fun encodeToWire(buffer: Buffer, msg: M) {
        val json: String = mapper.toJson(msg)
        buffer.appendInt(json.toByteArray().size)
        buffer.appendString(json)
    }

    override fun systemCodecID(): Byte = -1

    override fun name(): String = "${this.javaClass.canonicalName}-$clazz"
}

/**
 * This is the place where new messages should be connected.
 * Don't forget to call this method in the very vertx's initialization process.
 */
fun registerCodecs(bus: EventBus) {
    bus.registerDefaultCodec(EnemyConnectedMessage::class.java, RoomMessageCodec(EnemyConnectedMessage::class.java))
    bus.registerDefaultCodec(EnemyDisconnectedMessage::class.java, RoomMessageCodec(EnemyDisconnectedMessage::class.java))
    bus.registerDefaultCodec(GameStartedMessage::class.java, RoomMessageCodec(GameStartedMessage::class.java))
    bus.registerDefaultCodec(RoundEndsMessage::class.java, RoomMessageCodec(RoundEndsMessage::class.java))
    bus.registerDefaultCodec(HelloMessage::class.java, RoomMessageCodec(HelloMessage::class.java))
    bus.registerDefaultCodec(ServerHelloMessage::class.java, RoomMessageCodec(ServerHelloMessage::class.java))
    bus.registerDefaultCodec(MoveMessage::class.java, RoomMessageCodec(MoveMessage::class.java))
    bus.registerDefaultCodec(GameStateMessage::class.java, RoomMessageCodec(GameStateMessage::class.java))
    bus.registerDefaultCodec(SoundMessage::class.java, RoomMessageCodec(SoundMessage::class.java))
    bus.registerDefaultCodec(ThrowBrickMessage::class.java, RoomMessageCodec(ThrowBrickMessage::class.java))
}
