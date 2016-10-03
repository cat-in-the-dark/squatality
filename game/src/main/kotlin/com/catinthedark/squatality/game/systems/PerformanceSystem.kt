package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label

class PerformanceSystem(
    private val hudStage: Stage
) : IntervalSystem(0.03f) {
    private val font = BitmapFont()
    private val label = Label(" ", Label.LabelStyle(font, Color.WHITE))

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        label.fontScaleX = 2f
        label.fontScaleY = 2f
        hudStage.addActor(label)
    }

    override fun updateInterval() {
        label.setText(" FPS: ${Gdx.graphics.framesPerSecond}")
    }
}
