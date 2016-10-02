package com.catinthedark.squatality.game

import java.net.URI
import java.util.*

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
        val tickRate = 20f
        val syncDelay: Float = 1f / tickRate
    }

    object Names {
        private val names = listOf("Anon", "Anonas", "Anton", "Antonina", "Nanas", "Adidos", "Pipos", "Mimos", "Pisos", "Anonim", "Antonim", "Zasos", "Abibas", "Bibos", "Poltos")
        fun random() = names[Random().nextInt(names.size)]
    }

}
