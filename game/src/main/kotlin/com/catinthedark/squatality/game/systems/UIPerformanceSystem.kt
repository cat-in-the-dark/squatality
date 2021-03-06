package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label

class UIPerformanceSystem(
    private val hudStage: Stage,
    private val syncDelta: () -> Long,
    private val lerpDelay: () -> Long,
    private val latency: () -> Int
) : IntervalSystem(0.1f) {
    private val font = BitmapFont()
    private val label = Label(" ", Label.LabelStyle(font, Color.WHITE))

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        label.fontScaleX = 2f
        label.fontScaleY = 2f
        hudStage.addActor(label)
    }

    override fun removedFromEngine(engine: Engine?) {
        label.remove()
        super.removedFromEngine(engine)
    }

    override fun updateInterval() {
        label.setText(" FPS: ${Gdx.graphics.framesPerSecond}\t SyncDelta: ${syncDelta()} \tLerpDelay: ${lerpDelay()} \tPing: ${latency()}")
    }
}
