package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.AnimationComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.TextureComponent

class AnimationSystem: IteratingSystem(
    Family.all(TextureComponent::class.java, AnimationComponent::class.java, StateComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val tex = Mappers.texture[entity]
        val anim = Mappers.animation[entity]
        val state = Mappers.state[entity]

        val animation = anim.animations[state.state]
        if (animation != null) {
            tex.region = animation.getKeyFrame(state.time)
        }
    }
}
