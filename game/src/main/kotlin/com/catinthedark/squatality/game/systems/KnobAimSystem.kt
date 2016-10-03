package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.KnobComponent

class KnobAimSystem(): IteratingSystem(
    Family.all(KnobComponent::class.java, AimComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val kc = Mappers.knob[entity] ?: return
        val ac = Mappers.aim[entity] ?: return
        val tp = kc.touchPad ?: return
        // do some stuff
    }
}
