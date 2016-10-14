package com.catinthedark.squatality.server

object Configs {
    private val pb = ProcessBuilder()

    val port: Int = try {
        pb.environment()["PORT"]?.toInt()
    } catch(e: Exception) {
        null
    } ?: 8080

    val tcpPort: Int = try {
        pb.environment()["TCP_PORT"]?.toInt()
    } catch (e: Exception) {
        null
    } ?: 54555

    val udpPort: Int = try {
        pb.environment()["UDP_PORT"]?.toInt()
    } catch (e: Exception) {
        null
    } ?: 54777
}
