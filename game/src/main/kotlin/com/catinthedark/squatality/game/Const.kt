package com.catinthedark.squatality.game

import java.net.URI

object Const {
    object Screen {
        val WIDTH = 1280
        val HEIGHT = 720
        val ZOOM = 1f
    }

    object UI {
        val animationSpeed = 0.2f
    }

    object Network {
        val server: URI = URI.create("https://squatality-test.herokuapp.com/")
    }
}
