package com.catinthedark.squatality.game

import com.badlogic.gdx.Gdx
import com.catinthedark.lib.IMessage
import com.catinthedark.lib.MessageBus
import com.catinthedark.lib.Observable
import com.catinthedark.lib.network.SocketIOTransport
import com.catinthedark.squatality.models.*
import java.net.URI

class NetworkControl(serverAddress: URI): Runnable {
    init {
        MessageConverter.register(SocketIOTransport.DisconnectMessage::class.java)
        MessageConverter.register(SocketIOTransport.ConnectErrorMessage::class.java)
        MessageConverter.register(SocketIOTransport.ConnectMessage::class.java)
        MessageConverter.register(SocketIOTransport.ReConnectMessage::class.java)
    }

    val onConnected = Observable<SocketIOTransport.ConnectMessage>()
    val onServerHello = Observable<ServerHelloMessage>()
    val onGameState = Observable<GameStateModel>()
    val onGameStarted = Observable<GameStartedMessage>()
    val onEnemyConnected = Observable<EnemyConnectedMessage>()
    val onEnemyDisconnected = Observable<EnemyDisconnectedMessage>()
    val sender: (IMessage) -> Unit = {
        messageBus.send(it)
    }

    private val TAG = "NetworkControl"
    private val transport = SocketIOTransport(MessageConverter.parser, serverAddress)
    private val messageBus = MessageBus(transport).apply {
        subscribe(SocketIOTransport.ConnectMessage::class.java, {
            Gdx.app.log(TAG, "Connected ${it.id}")
            onConnected(it)
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
            Gdx.app.log(TAG, "$it")
            onGameStarted(it)
        })
        subscribe(ServerHelloMessage::class.java, {
            Gdx.app.log(TAG, "$it")
            onServerHello(it)
        })
        subscribe(GameStateMessage::class.java, {
            onGameState(it.gameStateModel)
        })
        subscribe(EnemyConnectedMessage::class.java, {
            Gdx.app.log(TAG, "EnemyConnectedMessage $it")
            onEnemyConnected(it)
        })
        subscribe(EnemyDisconnectedMessage::class.java, {
            Gdx.app.log(TAG, "EnemyDisconnectedMessage $it")
            onEnemyDisconnected(it)
        })
    }

    override fun run() {
        Gdx.app.log(TAG, "Connecting")
        transport.connect()
    }

    fun dispose() {
        transport.disconnect()
    }
}
