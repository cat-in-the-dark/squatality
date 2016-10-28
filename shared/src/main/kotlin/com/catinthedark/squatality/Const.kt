package com.catinthedark.squatality

import com.catinthedark.math.Vector2
import java.util.*

object Const {
    object UI {
        val horizontalBorderWidth = 235f
        val verticalBorderWidth = 235f
        val fieldWidth = 1081f
        val fieldHeight = 652f

        val playerSkinNames = listOf(
            "gop_black",
            "gop_blue",
            "gop_green",
            "gop_red"
        )

        fun randomSkin(): String =
            playerSkinNames[Random().nextInt(playerSkinNames.size)]
    }

    object Bonus {
        val hat = "hat"
    }

    object Balance {
        val maxPlayersInRoom = 10
        val hatRadius = 20f
        val roundTime: Long = 120 * 1000 // milliseconds
        val shootRageSpeed = 0.5f
        val maxShootRage = 80f
        val minShootRange = 20f
        val playerSpeed = 5.0f
        val playerSpeedBonus = 10.0f
        val playerRadius = 40.0f
        val brickRadius = 10.0f
        val brickFriction = 1f
        val spawnPoints: List<Vector2> = (2..9).flatMap { x ->
            (1..6).map { y ->
                Vector2(UI.verticalBorderWidth + x * 100f, UI.horizontalBorderWidth + y * 100f)
            }
        }

        fun randomSpawn(): Vector2 {
            val ab = spawnPoints[Random().nextInt(spawnPoints.size)]
            return ab.copy()
        }

        val bonusDelay = 20L
        val bonusesAtOnce = 2
        fun randomBonus() = Bonus.hat
    }

    object Network {
        object Server {
            val tickRate = 20f
            val tickDelay = (1000f / tickRate).toLong() // in milliseconds
        }
        object Client {
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
    }
}
