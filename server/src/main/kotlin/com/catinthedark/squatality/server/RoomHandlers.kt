package com.catinthedark.squatality.server

import com.catinthedark.squatality.models.HelloMessage
import com.catinthedark.squatality.models.MoveMessage
import com.catinthedark.squatality.models.State
import com.catinthedark.squatality.models.ThrowBrickMessage
import java.util.*

/**
 * This interface will be used as thread safe wrapper.
 * Call this function only by invoke method from InvokeWrapper.
 * @see com.catinthedark.lib.invoker.InvokeWrapper
 */
interface RoomHandlers {
    fun onMove(msg: MoveMessage, clientID: UUID)
    fun onHello(msg: HelloMessage, clientID: UUID): UUID?
    fun onThrowBrick(msg: ThrowBrickMessage, clientID: UUID)
    fun onDisconnect(clientID: UUID)
    fun onTick(deltaTime: Long)
    fun onSpawnBonus()
    fun onFunc(func: () -> Unit)
}
