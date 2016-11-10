package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.IMessage
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.KnobComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.models.State
import com.catinthedark.squatality.models.ThrowBrickMessage

class KnobAimSystem(
    private val hudStage: Stage,
    private val send: (IMessage) -> Unit
) : IteratingSystem(
    Family.all(KnobComponent::class.java, AimComponent::class.java, StateComponent::class.java, TransformComponent::class.java).get()
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
        val ac = Mappers.aim[entity] ?: return
        val sc = Mappers.state[entity] ?: return
        val tc = Mappers.transform[entity] ?: return
        val tp = kc.touchPad ?: return
        val point = Vector2(tp.knobPercentX, tp.knobPercentY)

        if (tp.isTouched) {
            ac.angle = point.angle() - 90
            ac.aiming = true
            if (sc.hasBrick && sc.state == State.IDLE.name) {
                if (ac.force < Const.Balance.minShootRange) {
                    ac.force = Const.Balance.minShootRange
                } else if (ac.force < Const.Balance.maxShootRage) {
                    ac.force += Const.Balance.shootRageSpeed
                }
            } else {
                ac.force = 0f // prevent keeping unused force :)
            }
        } else {
            ac.aiming = false
            if (ac.force >= Const.Balance.minShootRange && sc.hasBrick) {
                throwBrick(tc.pos, ac.angle, ac.force)
                sc.hasBrick = false
                ac.force = 0f
            }
        }
    }

    private fun throwBrick(pos: Vector3, angle: Float, force: Float) {
        val d = Const.Balance.playerRadius + Const.Balance.brickRadius + 5
        val a = Math.toRadians(angle.toDouble())
        val brickX = pos.x - d * Math.sin(a).toFloat()
        val brickY = pos.y + d * Math.cos(a).toFloat()
        send(ThrowBrickMessage(brickX, brickY, force, angle.toDouble()))
    }
}
