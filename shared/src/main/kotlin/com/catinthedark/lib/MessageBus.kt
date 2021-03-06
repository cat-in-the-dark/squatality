package com.catinthedark.lib

import org.slf4j.LoggerFactory

/**
 * MessageBus is a class to connect systems.
 * The way how systems actually connected is defined in [Transport] implementations.
 * @see Transport
 * @param transport the implementation responsible for certain message delivery.
 */
class MessageBus(
    private val transport: Transport
) {
    private val log = LoggerFactory.getLogger(MessageBus::class.java)
    private val subscribers: MutableMap<Class<out IMessage>, MutableList<(Any) -> Unit>> = hashMapOf()

    init {
        transport.setReceiver { msg ->
            val subs = subscribers[msg.javaClass]
            if (subs == null || subs.isEmpty()) {
                log.warn("There is no subscriber for ${msg.javaClass.canonicalName}")
            } else {
                subs.forEach { subscriber ->
                    subscriber(msg)
                }
            }
        }
    }

    /**
     * Publish message to other systems.
     * @param msg is message to publish.
     */
    fun send(msg: IMessage, withAck: Boolean = true) {
        transport.send(msg, withAck)
    }

    /**
     * Subscribe for messages with class T in its body.
     * @param T is class we observe.
     * @param clazz is the meta class of class we observe.
     * @param callback function will be called if message of type T is received.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IMessage> subscribe(clazz: Class<T>, callback: (T) -> Unit) {
        subscribers
            .getOrPut(clazz, { arrayListOf() })
            .add(callback as (Any) -> Unit)
    }
}
