package com.catinthedark.squatality.server

import io.vertx.core.AbstractVerticle
import io.vertx.core.logging.LoggerFactory


class MainVerticle: AbstractVerticle() {
    val logger = LoggerFactory.getLogger(MainVerticle::class.java)!!

    override fun start() {
        logger.info("MainVerticle started")
        registerCodecs(vertx.eventBus())
        vertx.deployVerticle(KryoVerticle())
    }
}
