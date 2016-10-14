package com.catinthedark.squatality.game

import com.catinthedark.lib.network.ConnectionOptions
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
        val server: ConnectionOptions = ConnectionOptions("https", "squatality-test.herokuapp.com", 80, 80)
        val localServer: ConnectionOptions = ConnectionOptions(null, "localhost", 54555, 54777)
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
