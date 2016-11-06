package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.NetworkControl
import com.catinthedark.squatality.models.ServerHelloMessage

class PairingScreen(
    private val stage: Stage,
    private val nc: NetworkControl
) : YieldUnit<AssetManager, AssetManager> {
    private lateinit var am: AssetManager
    private var hello: ServerHelloMessage? = null

    override fun onExit() {

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
        nc.start()
    }

    override fun run(delta: Float): AssetManager? {
        if (hello != null) {
            hello = null
            nc.clearSubscriptions()
            return am
        }
        stage.batch.managed {
            it.draw(am.get(Assets.Names.PAIRING, Texture::class.java), 0f, 0f)
        }
        stage.draw()
        return null
    }
}
