package com.catinthedark.squatality.server

import org.slf4j.LoggerFactory

object ServerLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val LOG = LoggerFactory.getLogger(ServerLauncher::class.java)
        LOG.info("Start Squatality server")

        val server = KryoService()
        server.start()
    }
}
