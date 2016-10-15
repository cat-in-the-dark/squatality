package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.catinthedark.lib.IMessage
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.RemoteMoveComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.models.MoveMessage
import com.catinthedark.squatality.models.State

class RemoteMovementSystem(
    private val send: (IMessage) -> Unit
) : IteratingSystem(
    Family.all(MoveComponent::class.java, RemoteMoveComponent::class.java, StateComponent::class.java, AimComponent::class.java).get()
) {
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val mc = Mappers.movement[entity] ?: return
        val rtc = Mappers.remote.transform[entity] ?: return
        val sc = Mappers.state[entity] ?: return
        val ac = Mappers.aim[entity] ?: return

        rtc.lastSync += deltaTime
        rtc.velocity.x += deltaTime * mc.velocity.x
        rtc.velocity.y += deltaTime * mc.velocity.y

        if (ac.aiming) {
            rtc.angle = ac.angle
        } else if (mc.velocity.x != 0f || mc.velocity.y != 0f) {
            rtc.angle = mc.velocity.angle() - 90
        }

        sync(rtc)
    }

    private fun sync(rtc: RemoteMoveComponent) {
        if (rtc.lastSync < Const.Network.Client.syncDelay) return

        val state = if (rtc.velocity.isZero) {
            State.IDLE
        } else {
            State.RUNNING
        }
        send(MoveMessage(speedX = rtc.velocity.x, speedY = rtc.velocity.y, angle = rtc.angle, stateName = state.name))
        rtc.velocity.setZero()
        rtc.lastSync -= Const.Network.Client.syncDelay
    }
}
