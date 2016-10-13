package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.catinthedark.lib.IMessage
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.KnobComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.models.ThrowBrickMessage

class KnobAimSystem(
    private val send: (IMessage) -> Unit
) : IteratingSystem(
    Family.all(KnobComponent::class.java, AimComponent::class.java, StateComponent::class.java, TransformComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val kc = Mappers.knob[entity] ?: return
        val ac = Mappers.aim[entity] ?: return
        val sc = Mappers.state[entity] ?: return
        val tc = Mappers.transform[entity] ?: return
        val tp = kc.touchPad ?: return
        val point = Vector2(tp.knobPercentX, tp.knobPercentY)

        if (tp.knobPercentX != 0f || tp.knobPercentY != 0f) {
            ac.angle = point.angle() - 90
            ac.aiming = true
            if (sc.hasBrick) {
                ac.force = 40f
            }
        } else {
            ac.aiming = false
            if (ac.force > 0 && sc.hasBrick) {
                throwBrick(tc.pos, ac.angle, ac.force)
                sc.hasBrick = false
                ac.force = 0f
            }
        }
    }

    private fun throwBrick(pos: Vector3, angle: Float, force: Float) {
        send(ThrowBrickMessage(pos.x, pos.y, force, angle.toDouble()))
    }
}
