package com.catinthedark.squatality.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.Viewport
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed

class SplashScreen(
    private val batch: SpriteBatch
): YieldUnit<Viewport, AssetManager> {
    lateinit var am: AssetManager
    lateinit var viewport: Viewport

    override fun onActivate(data: Viewport) {
        viewport = data
        am = Assets.load()
    }

    override fun run(delta: Float): AssetManager? {
        if (am.update()) {
            return am
        }
        Gdx.app.log("SplashScreen", "Loading assets...${am.progress}")
        if (am.isLoaded(Assets.Names.LOGO, Texture::class.java)) {
            batch.projectionMatrix = viewport.camera.combined
            batch.managed {
                Gdx.app.log("SplashScreen", "Draw logo")
                it.draw(am.get(Assets.Names.LOGO, Texture::class.java), 0f, 0f)
            }
        }
        return null
    }

    override fun onExit() {

    }
}
