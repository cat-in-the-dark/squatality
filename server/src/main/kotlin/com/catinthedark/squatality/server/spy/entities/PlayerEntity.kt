package com.catinthedark.squatality.server.spy.entities

data class PlayerEntity(
    val id: String,
    val connectedAt: Long,
    val disconnectedAt: Long?,
    val ip: String?,
    val geo: GeoEntity?,
    val name: String,
    val frags: Int,
    val deaths: Int
)
