package com.catinthedark.lib

import org.slf4j.LoggerFactory

/**
 * This transport doesn't need a parser.
 * May be it's already implemented in a third party library, for example like it's in the KryoNet.
 */
abstract class StraightTransport: Transport {
    private val log = LoggerFactory.getLogger(StraightTransport::class.java)

    private var receiver: (IMessage) -> Unit = {
        log.warn("Receiver wasn't specified")
    }

    override fun send(msg: IMessage, withAck: Boolean) {
        try {
            remoteSend(msg, withAck)
        } catch (e: Exception) {
            log.error("Sending error: ${e.message}", e)
        }
    }

    override fun setReceiver(receiver: (IMessage) -> Unit) {
        this.receiver = receiver
    }

    protected fun onReceive(data: IMessage) {
        receiver(data)
    }

    protected abstract fun remoteSend(data: Any, withAck: Boolean = true)
}
