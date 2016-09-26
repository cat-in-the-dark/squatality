package com.catinthedark.squatality.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed

class TitleScreen(
    private val batch: SpriteBatch
): YieldUnit<AssetManager, AssetManager> {
    private lateinit var am: AssetManager

    private var time = 0f

    override fun onActivate(data: AssetManager) {
        am = data
        time = 0f
    }

    override fun run(delta: Float): AssetManager? {
        batch.managed {
            it.draw(am.get(Assets.Names.TITLE, Texture::class.java), 0f, 0f)
        }
        time += delta
        if (time > 0.5) return am
        return null
    }

    override fun onExit() {

    }
}