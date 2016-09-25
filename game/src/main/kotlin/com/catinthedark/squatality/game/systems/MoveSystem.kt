package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.TransformComponent

class MoveSystem: IteratingSystem(
    Family.all(MoveComponent::class.java, TransformComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val mc = Mappers.movement[entity]
        val tc = Mappers.transform[entity]

        tc.pos.x += deltaTime * mc.velocity.x
        tc.pos.y += deltaTime * mc.velocity.y
    }
}
