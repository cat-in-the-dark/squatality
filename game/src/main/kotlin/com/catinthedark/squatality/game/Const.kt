package com.catinthedark.squatality.game

import com.catinthedark.lib.network.ConnectionOptions
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
        val server: ConnectionOptions = ConnectionOptions(null, "95.213.200.68", 54555, 54777)
        val localServer: ConnectionOptions = ConnectionOptions(null, "localhost", 54555, 54777)
    }

    object Names {
        private val names = listOf("Anon", "Anonas", "Anton", "Antonina", "Nanas", "Adidos", "Pipos", "Mimos", "Pisos", "Anonim", "Antonim", "Zasos", "Abibas", "Bibos", "Poltos")
        fun random() = names[Random().nextInt(names.size)]
    }

}
