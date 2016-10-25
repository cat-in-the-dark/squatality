package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.FollowingTransformComponent
import com.catinthedark.squatality.game.components.TransformComponent

class FollowingTransformSystem : IteratingSystem(
    Family.all(TransformComponent::class.java, FollowingTransformComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val tc = Mappers.transform[entity] ?: return
        val ftc = Mappers.followingTransform[entity] ?: return

        ftc.strategy(ftc.target, tc)
    }
}
