package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.TransformComponent

class LocalMovementSystem : IteratingSystem(
    Family.all(MoveComponent::class.java, TransformComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val mc = Mappers.movement[entity] ?: return
        val tc = Mappers.transform[entity] ?: return

        tc.pos.x += deltaTime * mc.velocity.x
        tc.pos.y += deltaTime * mc.velocity.y
        if (mc.velocity.x != 0f || mc.velocity.y != 0f) {
            tc.angle = mc.velocity.angle() - 90
        }
    }
}
