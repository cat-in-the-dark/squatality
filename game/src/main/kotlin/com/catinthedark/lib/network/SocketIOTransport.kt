package com.catinthedark.lib.network

import com.catinthedark.lib.IMessage
import com.catinthedark.lib.Parser
import com.catinthedark.lib.RemoteTransport
import io.socket.client.IO
import io.socket.client.Socket.*
import java.net.URI

class SocketIOTransport(parser: Parser, uri: URI) : RemoteTransport(parser) {
    private val options = IO.Options().apply {
        forceNew = true
        reconnection = true
    }
    private val socket = IO.socket(uri, options).apply {
        on(EVENT_CONNECT, {
            onReceive(ConnectMessage(this.id()))
        })
        on(EVENT_RECONNECT, {
            onReceive(ReConnectMessage(this.id()))
        })
        on(EVENT_DISCONNECT, {
            onReceive(DisconnectMessage())
        })
        on(EVENT_CONNECT_ERROR, {
            onReceive(ConnectErrorMessage())
        })
        on(EVENT_MESSAGE, {
            val data = it.firstOrNull()
            if (data != null && data is String) {
                onReceive(data)
            }
        })
    }

    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    override fun remoteSend(data: String) {
        socket.send(data)
    }

    data class ReConnectMessage(val id: String) : IMessage
    data class ConnectMessage(val id: String) : IMessage
    class DisconnectMessage() : IMessage
    class ConnectErrorMessage() : IMessage
}
