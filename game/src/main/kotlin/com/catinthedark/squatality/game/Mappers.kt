package com.catinthedark.squatality.game

import com.badlogic.ashley.core.ComponentMapper
import com.catinthedark.squatality.game.components.*

object Mappers {
    val lerpTransform = ComponentMapper.getFor(LerpTransformComponent::class.java)!!
    val transform = ComponentMapper.getFor(TransformComponent::class.java)!!
    val texture = ComponentMapper.getFor(TextureComponent::class.java)!!
    val camera = ComponentMapper.getFor(CameraComponent::class.java)!!
    val animation = ComponentMapper.getFor(AnimationComponent::class.java)!!
    val state = ComponentMapper.getFor(StateComponent::class.java)!!
    val movement = ComponentMapper.getFor(MoveComponent::class.java)!!
    val knob = ComponentMapper.getFor(KnobComponent::class.java)!!
    val aim = ComponentMapper.getFor(AimComponent::class.java)!!
    object remote {
        val transform = ComponentMapper.getFor(RemoteMoveComponent::class.java)!!
        val id = ComponentMapper.getFor(RemoteIDComponent::class.java)!!
    }
}
