package com.catinthedark.squatality.server

import java.util.*

object Addressing {
    fun onMove(roomID: UUID) = "room-$roomID.onMove"
    fun onHello(roomID: UUID) = "room-$roomID.onHello"
    fun onThrowBrick(roomID: UUID) = "room-$roomID.onThrowBrick"
    fun onConnect(roomID: UUID) = "room-$roomID.onConnect"
    fun onDisconnect(roomID: UUID) = "room-$roomID.onDisconnect"
    fun onTick() = "room.onTick"

    fun onGameState() = "socket.onGameState"
    fun onGameStarted() = "socket.onGameStarted"
}
