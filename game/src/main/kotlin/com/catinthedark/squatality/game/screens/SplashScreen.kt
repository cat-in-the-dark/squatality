package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.services.SoundService

class SplashScreen(
    private val stage: Stage,
    private val soundService: SoundService
): YieldUnit<Unit, AssetManager> {
    lateinit var am: AssetManager

    override fun onActivate(data: Unit) {
        am = Assets.load()
    }

    override fun run(delta: Float): AssetManager? {
        if (am.update()) {
            soundService.register(am)
            return am
        } else {
            Gdx.app.log("SplashScreen", "Loading assets...${am.progress}")
        }
        if (am.isLoaded(Assets.Names.LOGO, Texture::class.java)) {
            stage.batch.managed {
                it.draw(am.get(Assets.Names.LOGO, Texture::class.java), 0f, 0f)
            }
        }
        stage.draw()

        return null
    }

    override fun onExit() {

    }
}
