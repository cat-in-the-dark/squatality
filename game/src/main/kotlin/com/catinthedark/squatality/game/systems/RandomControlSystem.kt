package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.MoveComponent
import java.util.*

class RandomControlSystem : IteratingSystem(
    Family.all(MoveComponent::class.java).get()
) {
    private var time = 0f
    private var dirX = 1f
    private var dirY = 1f
    private val rand = Random()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        time += deltaTime
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val mc = Mappers.movement[entity]

        if (time > 4) {
            time = 0f
            dirX = if (rand.nextBoolean()) { -1f } else { 1f }
            dirY = if (rand.nextBoolean()) { -1f } else { 1f }
        }
        mc.velocity.x = dirX * 30f
        mc.velocity.y = dirY * 20f
    }
}
