package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.CameraComponent

class CameraSystem(
): IteratingSystem(
    Family.all(CameraComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val cam = Mappers.camera[entity]
        val target = Mappers.texture[entity]
    }
}
