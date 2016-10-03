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
        val tex = Mappers.texture[entity] ?: return
        val anim = Mappers.animation[entity] ?: return
        val state = Mappers.state[entity] ?: return

        val animation = anim.animations[state.state] ?: return
        tex.region = animation.getKeyFrame(state.time)
    }
}
