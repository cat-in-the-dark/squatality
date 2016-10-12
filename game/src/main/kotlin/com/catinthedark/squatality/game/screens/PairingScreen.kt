package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.Const
import com.catinthedark.squatality.game.NetworkControl
import com.catinthedark.squatality.models.HelloMessage
import com.catinthedark.squatality.models.ServerHelloMessage
import io.socket.thread.EventThread

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
        EventThread.exec(nc)
        nc.onServerHello.subscribe {
            hello = it
        }
    }

    override fun run(delta: Float): AssetManager? {
        if (hello != null) {
            return am
        }
        stage.batch.managed {
            it.draw(am.get(Assets.Names.PAIRING, Texture::class.java), 0f, 0f)
        }
        stage.draw()
        return null
    }
}
