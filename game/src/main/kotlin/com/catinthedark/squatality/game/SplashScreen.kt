package com.catinthedark.squatality.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed

class SplashScreen(
    private val batch: SpriteBatch
): YieldUnit<Unit, AssetManager> {
    lateinit var am: AssetManager

    override fun onActivate(data: Unit) {
        am = Assets.load()
    }

    override fun run(delta: Float): AssetManager? {
        if (am.update()) {
            return am
        } else {
            Gdx.app.log("SplashScreen", "Loading assets...${am.progress}")
        }
        if (am.isLoaded(Assets.Names.LOGO, Texture::class.java)) {
            batch.managed {
                it.draw(am.get(Assets.Names.LOGO, Texture::class.java), 0f, 0f)
            }
        }

        return null
    }

    override fun onExit() {

    }
}
