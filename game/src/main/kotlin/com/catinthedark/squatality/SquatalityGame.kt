package com.catinthedark.squatality

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.RouteMachine

class SquatalityGame : Game() {
    lateinit var batch: SpriteBatch
    val rm = RouteMachine()

    override fun create() {
        batch = SpriteBatch()
        val splash = SplashScreen(batch)
        val game = GameScreen()

        rm.addRoute(splash, { game })
        rm.start(splash, Unit)
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        rm.run(Gdx.graphics.deltaTime)
        super.render()
    }

    override fun dispose() {
        super.dispose()
    }
}
