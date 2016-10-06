package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.LerpTransformComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.utils.TimeConverter

class LerpSystem : IteratingSystem(
    Family.all(TransformComponent::class.java, LerpTransformComponent::class.java).get()
) {
    private var lerpDelay: Long = 0
    val getLerpDelay: () -> Long = { lerpDelay }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val tc = Mappers.transform[entity] ?: return
        val lrc = Mappers.lerpTransform[entity] ?: return
        val elements = lrc.queue.pollWithOverweight(TimeConverter.secondsToMillis(deltaTime))
        lerpDelay = lrc.queue.weight()
        elements.forEach { el ->
//            Gdx.app.log("LerpSystem", "W:${el.weight} P:${el.percentage()}")
            tc.angle = MathUtils.lerpAngleDeg(tc.angle, el.payload.angle, el.percentage())
            tc.pos.x = MathUtils.lerp(el.payload.prevPos.x, el.payload.pos.x, el.percentage())
            tc.pos.y = MathUtils.lerp(el.payload.prevPos.y, el.payload.pos.y, el.percentage())
        }
    }
}
