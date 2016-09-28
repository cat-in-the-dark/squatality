package com.catinthedark.lib

import org.slf4j.LoggerFactory

/**
 * LocalTransport represents delivery system inside one application.
 * @see Transport
 */
class LocalTransport : Transport {
    private val log = LoggerFactory.getLogger(LocalTransport::class.java)

    private var receiver: (IMessage) -> Unit = {
        log.warn("Receiver wasn't specified")
    }

    override fun setReceiver(receiver: (IMessage) -> Unit) {
        this.receiver = receiver
    }

    override fun send(msg: IMessage) {
        receiver(msg)
    }
}
