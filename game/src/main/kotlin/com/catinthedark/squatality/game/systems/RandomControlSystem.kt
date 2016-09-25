package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.math.Vector2
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.MoveComponent
import java.util.*

class RandomControlSystem : IteratingSystem(
    Family.all(MoveComponent::class.java).get()
) {
    private var time = 0f
    private val rand = Random()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        time += deltaTime
        if (time > 2) time = 0f
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val mc = Mappers.movement[entity]

        if (time > 2 - deltaTime) {
            val dir = Vector2(rand.nextFloat() * 2f - 1f, rand.nextFloat() * 2f - 1f)
            mc.velocity.x = dir.x * 50f
            mc.velocity.y = dir.y * 30f
        }
    }
}
