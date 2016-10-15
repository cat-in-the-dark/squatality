package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.KnobComponent
import com.catinthedark.squatality.game.components.MoveComponent

class KnobMovementSystem(
    private val hudStage: Stage
) : IteratingSystem(
    Family.all(KnobComponent::class.java, MoveComponent::class.java).get()
) {
    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, object : EntityListener {
            override fun entityRemoved(entity: Entity?) {
                val kc = Mappers.knob[entity] ?: return
                kc.touchPad?.remove()
            }

            override fun entityAdded(entity: Entity?) {
                val kc = Mappers.knob[entity] ?: return
                hudStage.addActor(kc.touchPad)
            }
        })
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val kc = Mappers.knob[entity] ?: return
        val mc = Mappers.movement[entity] ?: return
        val tp = kc.touchPad ?: return

        mc.velocity.x = tp.knobPercentX * mc.acceleration.x
        mc.velocity.y = tp.knobPercentY * mc.acceleration.y
    }
}
