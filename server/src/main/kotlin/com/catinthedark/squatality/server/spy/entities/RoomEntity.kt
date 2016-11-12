package com.catinthedark.squatality.server.spy.entities

import java.util.*

data class RoomEntity(
    val id: UUID = UUID.randomUUID(),
    val startedAt: Long = Date().time,
    val finishedAt: Long? = null,
    val type: String = "DeathMatch",
    val players: List<PlayerEntity> = emptyList()
)
