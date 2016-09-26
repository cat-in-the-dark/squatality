package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.KnobComponent
import com.catinthedark.squatality.game.components.MoveComponent

class KnobMovementSystem(): IteratingSystem(
    Family.all(KnobComponent::class.java, MoveComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val kc = Mappers.knob[entity]
        val mc = Mappers.movement[entity]
        val tp = kc.touchPad
        if (tp != null) {
            mc.velocity.x = tp.knobPercentX * mc.acceleration.x
            mc.velocity.y = tp.knobPercentY * mc.acceleration.y
        }
    }
}
