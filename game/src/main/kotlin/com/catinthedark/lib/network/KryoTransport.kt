package com.catinthedark.lib.network

import com.catinthedark.lib.IMessage
import com.catinthedark.lib.StraightTransport
import com.catinthedark.lib.TimeCache
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryonet.Client
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.FrameworkMessage
import com.esotericsoftware.kryonet.Listener
import org.slf4j.LoggerFactory

class KryoTransport(
    setup: (Kryo) -> Unit,
    private val options: ConnectionOptions
) : StraightTransport(), NetworkConnector {
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
                if (data != null) {
                    if (data is FrameworkMessage.Ping) return // skip framework message

                    if (data is FrameworkMessage.KeepAlive) {
                        this@apply.stop()
                        return
                    }

                    if (data is IMessage) {
                        onReceive(data)
                    } else {
                        log.info("onReceived unknown data: $data")
                    }
                } else {
                    log.warn("onReceived null data")
                }
            }
        })
    }

    init {
        setup(client.kryo)
    }

    fun latency() = latencyCache.get() ?: 0

    private fun calcLatency(): Int {
        client.updateReturnTripTime()
        return client.returnTripTime
    }

    override fun remoteSend(data: Any, withAck: Boolean) {
        if (withAck) {
            client.sendTCP(data)
        } else {
            client.sendUDP(data)
        }
    }

    /**
     * @throws java.io.IOException in case of network problems
     */
    override fun connect() {
        client.start()
        client.connect(5000, options.host, options.portTcp, options.portUdp)
    }

    override fun disconnect() {
        client.stop()
    }
}
