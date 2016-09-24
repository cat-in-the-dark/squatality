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

    override fun onActivate(data: AssetManager) {
        am = data
    }

    override fun run(delta: Float): AssetManager? {
        batch.managed {
            it.draw(am.get(Assets.Names.TITLE, Texture::class.java), 0f, 0f)
        }
        return null
    }

    override fun onExit() {

    }
}
