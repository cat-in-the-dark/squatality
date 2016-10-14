package com.catinthedark.lib.network

import com.catinthedark.lib.Parser
import com.catinthedark.lib.RemoteTransport
import io.socket.client.IO
import io.socket.client.Socket.*
import java.net.URI

class SocketIOTransport(parser: Parser, connectionOptions: ConnectionOptions) : RemoteTransport(parser), NetworkConnector {
    private val options = IO.Options().apply {
        forceNew = true
        reconnection = true
    }
    private val socket = IO.socket(connectionOptions.uri, options).apply {
        on(EVENT_CONNECT, {
            onReceive(NetworkConnector.ConnectMessage(this.id()))
        })
        on(EVENT_RECONNECT, {
            onReceive(NetworkConnector.ReConnectMessage(this.id()))
        })
        on(EVENT_DISCONNECT, {
            onReceive(NetworkConnector.DisconnectMessage())
        })
        on(EVENT_CONNECT_ERROR, {
            onReceive(NetworkConnector.ConnectErrorMessage())
        })
        on(EVENT_MESSAGE, {
            val data = it.firstOrNull()
            if (data != null && data is String) {
                onReceive(data)
            }
        })
    }

    override fun connect() {
        socket.connect()
    }

    override fun disconnect() {
        socket.disconnect()
    }

    override fun remoteSend(data: String, withAck: Boolean) {
        socket.send(data)
    }
}
