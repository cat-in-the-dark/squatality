package com.catinthedark.squatality.server

import java.util.*

object Addressing {
    fun onMove(roomID: UUID) = "room-$roomID.onMove"
    fun onConnect(roomID: UUID) = "room-$roomID.onConnect"
    fun onDisconnect(roomID: UUID) = "room-$roomID.onDisconnect"
    fun onTick() = "room.onTick"
}