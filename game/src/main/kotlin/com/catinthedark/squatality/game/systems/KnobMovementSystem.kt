package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
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

        mc.prevVelocity.x = mc.velocity.x
        mc.prevVelocity.y = mc.velocity.y

        val angle = Vector2(tp.knobPercentX, tp.knobPercentY).angleRad()

        if (tp.knobPercentY != 0f) {
            mc.velocity.y = mc.acceleration.y * MathUtils.sin(angle)
        } else {
            mc.velocity.y = 0f
        }

        if (tp.knobPercentX != 0f) {
            mc.velocity.x = mc.acceleration.x * MathUtils.cos(angle)
        } else {
            mc.velocity.x = 0f
        }
    }
}
