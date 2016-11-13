package com.catinthedark.squatality.server.spy.entities

data class RoomEntity(
    val id: String,
    val startedAt: Long,
    val finishedAt: Long?,
    val type: String,
    val players: Map<String, PlayerEntity>
)
