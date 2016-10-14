package com.catinthedark.lib.network

import com.catinthedark.lib.IMessage
import java.net.URI

interface NetworkConnector {
    data class ReConnectMessage(val id: String) : IMessage
    data class ConnectMessage(val id: String) : IMessage
    class DisconnectMessage() : IMessage
    class ConnectErrorMessage() : IMessage

    fun connect()
    fun disconnect()
}

data class ConnectionOptions(
    val schema: String? = null,
    val host: String,
    val portTcp: Int,
    val portUdp: Int
) {
    val uri: URI
        get() = URI.create("$schema://$host:$portTcp/")
}
