package com.catinthedark.squatality.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.catinthedark.lib.IMessage
import com.catinthedark.lib.MessageBus
import com.catinthedark.lib.Observable
import com.catinthedark.lib.network.ConnectionOptions
import com.catinthedark.lib.network.KryoTransport
import com.catinthedark.lib.network.NetworkConnector
import com.catinthedark.squatality.models.*

class NetworkControl(serverAddress: ConnectionOptions) : Disposable {
    val onConnectionError = Observable<Throwable>()
    val onConnected = Observable<NetworkConnector.ConnectMessage>()
    val onDisconnected = Observable<NetworkConnector.DisconnectMessage>()
    val onServerHello = Observable<ServerHelloMessage>()
    val onGameState = Observable<GameStateModel>()
    val onGameStarted = Observable<GameStartedMessage>()
    val onEnemyConnected = Observable<EnemyConnectedMessage>()
    val onEnemyDisconnected = Observable<EnemyDisconnectedMessage>()
    val onKilled = Observable<KillMessage>()
    val onRoundEnds = Observable<RoundEndsMessage>()

    val sender: (IMessage) -> Unit = {
        messageBus.send(it, true)
    }
    val senderUnreliable: (IMessage) -> Unit = {
        messageBus.send(it, false)
    }
    val latency: () -> Int = {
        transport.latency()
    }

    private val TAG = "NetworkControl"
    private val transport = KryoTransport(MessageConverter.kryoRegister, serverAddress)
    private val messageBus = MessageBus(transport).apply {
        subscribe(NetworkConnector.ConnectMessage::class.java, {
            Gdx.app.log(TAG, "Connected ${it.id}")
            onConnected(it)
        })
        subscribe(NetworkConnector.ConnectErrorMessage::class.java, {
            Gdx.app.log(TAG, "Connection error")
        })
        subscribe(NetworkConnector.ReConnectMessage::class.java, {
            Gdx.app.log(TAG, "Reconnected ${it.id}")
        })
        subscribe(NetworkConnector.DisconnectMessage::class.java, {
            Gdx.app.log(TAG, "Disconnected")
            onDisconnected(it)
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
        subscribe(KillMessage::class.java, {
            Gdx.app.log(TAG, "KillMessage $it")
            onKilled(it)
        })
        subscribe(RoundEndsMessage::class.java, {
            Gdx.app.log(TAG, "RoundEndsMessage $it")
            onRoundEnds(it)
        })
    }

    fun start() {
        Gdx.app.log(TAG, "Connecting")
        try {
            transport.connect()
        } catch (e: Exception) {
            onConnectionError(e)
        }
    }

    override fun dispose() {
        transport.disconnect()
        clearSubscriptions()
    }

    fun clearSubscriptions() {
        onConnected.clear()
        onConnectionError.clear()
        onDisconnected.clear()
        onServerHello.clear()
        onGameState.clear()
        onGameStarted.clear()
        onEnemyConnected.clear()
        onEnemyDisconnected.clear()
        onKilled.clear()
        onRoundEnds.clear()
    }
}
