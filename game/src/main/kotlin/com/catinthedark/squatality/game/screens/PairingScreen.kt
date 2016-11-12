package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.NetworkControl
import com.catinthedark.squatality.game.screens.messages.PairingMessage
import com.catinthedark.squatality.models.ServerHelloMessage
import com.catinthedark.squatality.models.VERSION
import java.util.*
import kotlin.concurrent.schedule

class PairingScreen(
    private val stage: Stage,
    private val nc: NetworkControl
) : YieldUnit<AssetManager, PairingMessage> {
    private lateinit var am: AssetManager
    private var hello: ServerHelloMessage? = null
    private val timer: Timer = Timer(true)
    private var lastTask: TimerTask? = null

    override fun onExit() {
        lastTask?.cancel()
    }

    override fun onActivate(data: AssetManager) {
        am = data
        nc.onServerHello.subscribe {
            hello = it
        }
        nc.onDisconnected.subscribe { msg ->
            nc.dispose()
            onActivate(data) // Try again
        }
        nc.onConnectionError.subscribe { err ->
            Gdx.app.error("PairingScreen", "Connection error: ${err.message}")
            lastTask = timer.schedule(1000L, {
                Gdx.app.log("PairingScreen", "Reconnecting")
                nc.start()
            })
        }
        nc.start()
    }

    override fun run(delta: Float): PairingMessage? {
        if (hello != null) {
            val v = hello?.version
            nc.clearSubscriptions()
            hello = null

            if (v != VERSION) {
                return PairingMessage(am, true)
            } else {
                return PairingMessage(am, false)
            }
        }
        stage.batch.managed {
            it.draw(am.get(Assets.Names.PAIRING, Texture::class.java), 0f, 0f)
        }
        stage.draw()
        return null
    }
}
