package com.catinthedark.lib.network

import com.catinthedark.lib.Parser
import com.catinthedark.lib.RemoteTransport
import com.catinthedark.lib.TimeCache
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.Listener
import org.slf4j.LoggerFactory

class KryoTransport(
    parser: Parser,
    private val options: ConnectionOptions
) : RemoteTransport(parser), NetworkConnector {
    private val log = LoggerFactory.getLogger(KryoTransport::class.java)
    private val latencyCache = TimeCache({
        calcLatency()
    }, 1000L)

    private val client = Client().apply {
        addListener(object : Listener() {
            override fun connected(connection: Connection?) {
                if (connection == null) {
                    log.warn("onConnected get null connection!")
                    return
                }
                onReceive(NetworkConnector.ConnectMessage(connection.toString()))
            }

            override fun disconnected(connection: Connection?) {
                onReceive(NetworkConnector.DisconnectMessage())
            }

            override fun received(connection: Connection?, data: Any?) {
                if (connection == null) {
                    log.warn("onReceived get null connection!")
                    return
                }
                if (data != null && data is String) {
                    onReceive(data)
                } else {
                    log.warn("onReceived unknown data: $data")
                }
            }
        })
    }

    fun latency() = latencyCache.get() ?: 0

    private fun calcLatency(): Int {
        client.updateReturnTripTime()
        return client.returnTripTime
    }

    override fun remoteSend(data: String, withAck: Boolean) {
        if (withAck) {
            client.sendTCP(data)
        } else {
            client.sendUDP(data)
        }
    }

    override fun connect() {
        client.start()
        client.connect(5000, options.host, options.portTcp, options.portUdp)
    }

    override fun disconnect() {
        client.stop()
    }
}
