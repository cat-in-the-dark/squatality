package com.catinthedark.squatality.server.spy.entities

import java.util.*

data class PlayerEntity(
    val id: UUID,
    val connectedAt: Long,
    val disconnectedAt: Long?,
    val ip: String?,
    val geo: GeoEntity?,
    val name: String,
    val frags: Int,
    val deaths: Int
)
