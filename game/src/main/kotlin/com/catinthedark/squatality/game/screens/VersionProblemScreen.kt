package com.catinthedark.squatality.game.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.screens.messages.PairingMessage

class VersionProblemScreen(
    private val hudStage: Stage
) : YieldUnit<PairingMessage, PairingMessage> {
    private lateinit var data: PairingMessage

    override fun onActivate(data: PairingMessage) {
        this.data = data
    }

    override fun run(delta: Float): PairingMessage? {
        hudStage.batch.managed {
            it.draw(data.am.get(Assets.Names.VERSION_ERROR, Texture::class.java), 0f, 0f)
        }
        hudStage.draw()

        return null
    }

    override fun onExit() {
        hudStage.dispose()
    }
}
