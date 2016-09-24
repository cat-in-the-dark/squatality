package com.catinthedark.squatality.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.catinthedark.lib.RouteMachine

class SquatalityGame : Game() {
    lateinit var batch: SpriteBatch
    val rm = RouteMachine()
    lateinit var viewport: ExtendViewport

    override fun create() {
        batch = SpriteBatch()
        viewport = ExtendViewport(
            Const.Screen.WIDTH / Const.Screen.ZOOM,
            Const.Screen.HEIGHT / Const.Screen.ZOOM,
            OrthographicCamera())

        val splash = SplashScreen(batch)
        val game = GameScreen()

        rm.addRoute(splash, { game })
        rm.start(splash, viewport)
    }

    override fun render() {
        Gdx.gl.glClearColor(255f,255f,255f,0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        viewport.apply(true)
        rm.run(Gdx.graphics.deltaTime)
        super.render()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height)
    }

    override fun dispose() {
        super.dispose()
    }
}
