package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.ClockComponent

class ClockSystem(
    private val hudStage: Stage
): IntervalSystem(0.5f) {
    private val family = Family.all(ClockComponent::class.java).get()
    private val font = BitmapFont()
    private val label = Label(" ", Label.LabelStyle(font, Color.WHITE))

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        label.fontScaleX = 2f
        label.fontScaleY = 2f
        label.x = 640f
        label.y = 640f
        hudStage.addActor(label)
    }

    override fun updateInterval() {
        val entity = engine.getEntitiesFor(family).firstOrNull() ?: return
        val cc = Mappers.clock[entity] ?: return
        label.setText(formatTime(cc.time))
    }

    private fun formatTime(time: Long): String {
        val min = time / 60
        val sec = time % 60
        return "$min:$sec"
    }
}
