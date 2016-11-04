package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.UINotificationComponent

class UINotificationsSystem(
    private val hudStage: Stage
) : IntervalSystem(0.1f) {
    private val family = Family.all(UINotificationComponent::class.java).get()
    private val font = BitmapFont()
    private val label = Label(" ", Label.LabelStyle(font, Color.WHITE))
    private val container = Container(label)

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        label.fontScaleX = 2f
        label.fontScaleY = 2f
        container.left()
        container.top()
        container.x = 880f
        container.y = 720f
        hudStage.addActor(container)
    }

    override fun removedFromEngine(engine: Engine?) {
        container.remove()
        super.removedFromEngine(engine)
    }

    override fun updateInterval() {
        val entity = engine.getEntitiesFor(family).firstOrNull() ?: return
        val nc = Mappers.notifications[entity] ?: return
        val text = StringBuilder()
        nc.list.forEach {
            it.ttl -= 1
            text.append("${it.text}\n")
        }
        nc.list.removeAll { it.ttl < 0 }
        label.setText(text)
    }
}
