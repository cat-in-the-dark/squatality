package com.catinthedark.squatality.game

import com.badlogic.gdx.Gdx
import com.catinthedark.lib.MessageBus
import com.catinthedark.lib.network.SocketIOTransport
import com.catinthedark.squatality.models.GameStartedMessage
import com.catinthedark.squatality.models.MessageConverter
import com.catinthedark.squatality.models.ServerHelloMessage
import java.net.URI

class NetworkControl(serverAddress: URI): Runnable {
    init {
        MessageConverter.register(SocketIOTransport.DisconnectMessage::class.java)
        MessageConverter.register(SocketIOTransport.ConnectErrorMessage::class.java)
        MessageConverter.register(SocketIOTransport.ConnectMessage::class.java)
        MessageConverter.register(SocketIOTransport.ReConnectMessage::class.java)
    }

    private val TAG = "NetworkControl"
    private val transport = SocketIOTransport(MessageConverter.parser, serverAddress)
    private val messageBus = MessageBus(transport).apply {
        subscribe(SocketIOTransport.ConnectMessage::class.java, {
            Gdx.app.log(TAG, "Connected ${it.id}")
        })
        subscribe(SocketIOTransport.ConnectErrorMessage::class.java, {
            Gdx.app.log(TAG, "Connection error")
        })
        subscribe(SocketIOTransport.ReConnectMessage::class.java, {
            Gdx.app.log(TAG, "Reconnected ${it.id}")
        })
        subscribe(SocketIOTransport.DisconnectMessage::class.java, {
            Gdx.app.log(TAG, "Disconnected")
        })
        subscribe(GameStartedMessage::class.java, {

        })
        subscribe(ServerHelloMessage::class.java, {
            Gdx.app.log(TAG, "$it")
        })
    }

    override fun run() {
        transport.connect()
    }

    fun dispose() {
        transport.disconnect()
    }
}
