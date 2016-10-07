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
        //val server: URI = URI.create("http://localhost:8080/")
        val tickRate = 40f
        val syncDelay: Float = 1f / tickRate // in seconds
        /**
         * Using GSM we have msg delay of 200-300ms, so constrain in 200ms obviously make movements sharp, but precise!
         * We can set lerp delay in 500ms and more, so moves will be smooth but very delayed.
         * It's kind of trade-off
         * Using WiFi we have msg delay of 60-120ms, so lerpDelay should not be reached
         * This const can me player-configurable prior to his skills and decision - sharp or smooth game.
         */
        val lerpDelay: Long = 1500 // in ms
    }

    object Names {
        private val names = listOf("Anon", "Anonas", "Anton", "Antonina", "Nanas", "Adidos", "Pipos", "Mimos", "Pisos", "Anonim", "Antonim", "Zasos", "Abibas", "Bibos", "Poltos")
        fun random() = names[Random().nextInt(names.size)]
    }

}
