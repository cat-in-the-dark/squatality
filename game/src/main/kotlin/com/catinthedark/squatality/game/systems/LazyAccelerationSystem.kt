package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.MoveComponent

/**
 * This system change player's acceleration based on his
 */
class LazyAccelerationSystem : IteratingSystem(
    Family.all(MoveComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val mc = Mappers.movement[entity] ?: return

        if (mc.velocity.x == 0f) {
            mc.acceleration.x = Const.Balance.playerSpeed.x
            return
        }
        if (mc.velocity.y == 0f) {
            mc.acceleration.y = Const.Balance.playerSpeed.y
            return
        }

        if (Math.abs(mc.prevVelocity.angle() - mc.velocity.angle()) <= Const.Balance.playerSpeedDropAngle) {
            // May be it should be non-linear algorithm?
            if (mc.acceleration.x <= Const.Balance.playerSpeedMax.x) {
                mc.acceleration.x += Const.Balance.playerAcceleration
            } else {
                mc.acceleration.x = Const.Balance.playerSpeedMax.x
            }
            if (mc.acceleration.y <= Const.Balance.playerSpeedMax.y) {
                mc.acceleration.y += Const.Balance.playerAcceleration
            } else {
                mc.acceleration.y = Const.Balance.playerSpeedMax.y
            }
        } else {
            mc.acceleration.x = Const.Balance.playerSpeed.x
            mc.acceleration.y = Const.Balance.playerSpeed.y
        }
    }
}
