package com.catinthedark.lib

import org.slf4j.LoggerFactory

abstract class RemoteTransport(val parser: Parser): Transport {
    private val log = LoggerFactory.getLogger(RemoteTransport::class.java)

    private var receiver: (IMessage) -> Unit = {
        log.warn("Receiver wasn't specified")
    }

    override fun send(msg: IMessage) {
        try {
            val data = parser.wrap(msg)
            remoteSend(data)
        } catch (e: Exception) {
            log.error("Sending error: ${e.message}", e)
        }
    }

    override fun setReceiver(receiver: (IMessage) -> Unit) {
        this.receiver = receiver
    }

    protected fun onReceive(data: String) {
        try {
            val msg = parser.unwrap(data)
            receiver(msg)
        } catch (e: Exception) {
            log.error("Receiving error: ${e.message}", e)
        }
    }

    protected fun onReceive(msg: IMessage) {
        receiver(msg)
    }

    protected abstract fun remoteSend(data: String)
}
