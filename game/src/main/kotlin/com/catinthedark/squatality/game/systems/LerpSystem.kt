package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.LerpTransformComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.utils.TimeConverter

class LerpSystem: IteratingSystem(
    Family.all(TransformComponent::class.java, LerpTransformComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val tc = Mappers.transform[entity] ?: return
        val lrc = Mappers.lerpTransform[entity] ?: return
        val el = lrc.queue.poll(TimeConverter.secondsToMillis(deltaTime)) ?: return
        println("${lrc.queue.weight()}")
        tc.pos.x = el.pos.x
        tc.pos.y = el.pos.y
        tc.angle = el.angle
    }
}
