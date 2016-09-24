package com.catinthedark.squatality.game

import com.badlogic.ashley.core.ComponentMapper
import com.catinthedark.squatality.game.components.*

object Mappers {
    val transform = ComponentMapper.getFor(TransformComponent::class.java)!!
    val texture = ComponentMapper.getFor(TextureComponent::class.java)!!
    val camera = ComponentMapper.getFor(CameraComponent::class.java)!!
    val animation = ComponentMapper.getFor(AnimationComponent::class.java)!!
    val state = ComponentMapper.getFor(StateComponent::class.java)!!
}
