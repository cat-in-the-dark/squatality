package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.screens.messages.StatsMessage
import com.catinthedark.squatality.game.services.StatsService

class StatsScreen(
    val hudStage: Stage
) : YieldUnit<StatsMessage, AssetManager> {
    private val service = StatsService(hudStage)
    private lateinit var data: StatsMessage
    private var shouldExit: Boolean = false
    private var timer = 0f

    val touchListener = object : ClickListener() {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            shouldExit = true
            return super.touchDown(event, x, y, pointer, button)
        }
    }

    override fun onActivate(data: StatsMessage) {
        this.data = data
        timer = 0f
        shouldExit = false
        service.register()
    }

    override fun run(delta: Float): AssetManager? {
        timer += delta
        if (timer >= 1) hudStage.addListener(touchListener) // prevent from auto click
        service.process(data.stats.players, data.stats.meId, true)
        hudStage.batch.managed {
            it.draw(data.am.get(Assets.Names.PAIRING, Texture::class.java), 0f, 0f)
        }
        hudStage.draw()
        if (shouldExit) return data.am
        return null
    }

    override fun onExit() {
        service.unregister()
        hudStage.removeListener(touchListener)
    }
}
