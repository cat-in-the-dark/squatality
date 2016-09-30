package com.catinthedark.squatality.server

object Configs {
    private val pb = ProcessBuilder()

    val port: Int = try {
        pb.environment()["PORT"]?.toInt()
    } catch(e: Exception) {
        null
    } ?: 8080
}
