package com.catinthedark.squatality.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.FPSLogger
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.catinthedark.lib.RouteMachine

class SquatalityGame : Game() {
    private lateinit var batch: SpriteBatch
    private val rm = RouteMachine()
    private lateinit var viewport: ExtendViewport
    private val fps = FPSLogger()

    override fun create() {
        batch = SpriteBatch()
        viewport = ExtendViewport(
            Const.Screen.WIDTH / Const.Screen.ZOOM,
            Const.Screen.HEIGHT / Const.Screen.ZOOM,
            OrthographicCamera())

        val splash = SplashScreen(batch)
        val title = TitleScreen(batch)
        val game = GameScreen(batch, viewport)

        rm.addRoute(splash, { title })
        rm.addRoute(title, { game })
        rm.start(splash, Unit)
    }

    override fun render() {
        Gdx.gl.glClearColor(255f,255f,255f,0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        viewport.apply(true)
        batch.projectionMatrix = viewport.camera.combined
        rm.run(Gdx.graphics.deltaTime)
        fps.log()
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
