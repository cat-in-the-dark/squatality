package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.StateComponent

class StateSystem: IteratingSystem(
    Family.all(StateComponent::class.java, MoveComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val state = Mappers.state[entity]
        val mc = Mappers.movement[entity]
        if (mc.velocity.isZero) {
            state.state = "IDLE"
        } else {
            state.state = "RUNNING"
        }

        if (state.state == "RUNNING") {
            state.time += deltaTime * 2
        } else {
            state.time += deltaTime
        }
    }
}
