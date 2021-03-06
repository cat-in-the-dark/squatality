package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets

class TitleScreen(
    private val stage: Stage
) : YieldUnit<AssetManager, AssetManager> {
    private lateinit var am: AssetManager

    private var time = 0f

    override fun onActivate(data: AssetManager) {
        am = data
        time = 0f
    }

    override fun run(delta: Float): AssetManager? {
        stage.batch.managed {
            it.draw(am.get(Assets.Names.TITLE, Texture::class.java), 0f, 0f)
        }
        stage.draw()
        time += delta
        if (time > 0.5) return am
        return null
    }

    override fun onExit() {

    }
}
