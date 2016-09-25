package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.squatality.game.components.KnobComponent

class KnobControlSystem(
    private val stage: Stage
): IteratingSystem(
    Family.all(KnobComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        stage.act(deltaTime)
    }
}
