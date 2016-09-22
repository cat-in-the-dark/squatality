package com.catinthedark.squatality

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.RouteMachine

class SquatalityGame : Game() {
    lateinit var batch: SpriteBatch
    val rm = RouteMachine()

    override fun create() {
        batch = SpriteBatch()
        val splash = SplashScreen()
        val game = GameScreen()

        rm.addRoute(splash, { game })
        rm.start(splash, Unit)
    }

    override fun render() {
        super.render()
        rm.run(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        super.dispose()
    }
}
