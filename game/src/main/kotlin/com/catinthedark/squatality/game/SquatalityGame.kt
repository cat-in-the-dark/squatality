package com.catinthedark.squatality.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.catinthedark.lib.RouteMachine
import com.catinthedark.lib.network.ConnectionOptions
import com.catinthedark.squatality.game.screens.*
import com.catinthedark.squatality.game.services.GameEventsRegistrar
import com.catinthedark.squatality.game.services.SoundService

class SquatalityGame(
    private val serverAddress: ConnectionOptions = Const.Network.server
) : Game() {
    private val rm = RouteMachine()

    private lateinit var stage: Stage
    private lateinit var hudStage: Stage
    private lateinit var nc: NetworkControl
    private val ger = GameEventsRegistrar()
    private val soundService = SoundService(ger)

    override fun create() {
        stage = Stage(FillViewport(
            Const.Screen.WIDTH / Const.Screen.ZOOM,
            Const.Screen.HEIGHT / Const.Screen.ZOOM,
            OrthographicCamera()), SpriteBatch())

        hudStage = Stage(FitViewport(
            Const.Screen.WIDTH / Const.Screen.ZOOM,
            Const.Screen.HEIGHT / Const.Screen.ZOOM,
            OrthographicCamera()), SpriteBatch())

        nc = NetworkControl(serverAddress)
        val splash = SplashScreen(hudStage, soundService)
        val title = TitleScreen(hudStage)
        val pairing = PairingScreen(hudStage, nc)
        val versionProblem = VersionProblemScreen(hudStage)
        val game = GameScreen(stage, hudStage, nc, ger)
        val stats = StatsScreen(hudStage)

        rm.addRoute(splash, { title })
        rm.addRoute(title, { pairing })
        rm.addRoute(pairing, {
            if (it.mismatch) {
                versionProblem
            } else {
                game
            }
        })
        rm.addRoute(game, { stats })
        rm.addRoute(stats, { title })
        rm.start(splash, Unit)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        hudStage.viewport.apply(true)
        stage.viewport.apply()
        hudStage.act(Gdx.graphics.deltaTime)
        stage.act(Gdx.graphics.deltaTime)
        rm.run(Gdx.graphics.deltaTime)
        super.render()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        hudStage.viewport.update(width, height, true)
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        nc.dispose()
        ger.dispose()
        soundService.dispose()
        super.dispose()
    }
}
