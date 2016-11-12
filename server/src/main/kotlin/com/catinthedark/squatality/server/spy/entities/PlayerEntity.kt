package com.catinthedark.squatality.server.spy.entities

import java.util.*

data class PlayerEntity(
    val id: UUID = UUID.randomUUID(),
    val connectedAt: Long = Date().time,
    val disconnectedAt: Long? = null,
    val ip: String = "",
    val geo: GeoEntity = GeoEntity(),
    val name: String = "",
    val frags: Int = 0,
    val deaths: Int = 0
)
