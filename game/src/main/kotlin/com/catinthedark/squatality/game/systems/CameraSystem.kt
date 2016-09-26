package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.CameraComponent
import com.catinthedark.squatality.game.components.TransformComponent

class CameraSystem(
    private val cam: Camera
) : IteratingSystem(
    Family.all(CameraComponent::class.java, TransformComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val targetC = Mappers.transform[entity]
        println("${cam.viewportWidth}: ${targetC.pos.x}")
        if (targetC.pos.x > cam.viewportWidth / 2) {
            cam.position.x = targetC.pos.x
        } else {
            cam.position.x = cam.viewportWidth / 2
        }

        if (targetC.pos.y > cam.viewportHeight / 2) {
            cam.position.y = targetC.pos.y
        } else {
            cam.position.y = cam.viewportHeight / 2
        }
    }
}
