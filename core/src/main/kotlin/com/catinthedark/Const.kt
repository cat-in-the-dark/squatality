package com.catinthedark

import com.catinthedark.math.Vector2
import java.util.*

object Const {
    object UI {
        val horizontalBorderWidth = 80f
        val verticalBorderWidth = 40f
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
        val hatRadius = 20f
        val roundTime: Long = 120 * 1000 // milliseconds
        val shootRageSpeed = 10f
        val maxShootRage = 80f
        val minShootRange = 20f
        val playerSpeed = 5.0f
        val playerSpeedBonus = 10.0f
        val playerRadius = 40.0f
        val brickRadius = 10.0f
        val brickFriction = 1f
        val spawnPoints: List<Vector2> = (2..9).flatMap { x ->
            (1..6).map { y ->
                Vector2(x * 100f, y * 100f)
            }
        }

        val spawnBrickPoints = listOf(
            Vector2(200f, 200f),
            Vector2(400f, 100f),
            Vector2(120f, 500f)
        )

        fun randomSpawn(): Vector2 {
            val ab = spawnPoints[Random().nextInt(spawnPoints.size)]
            return ab.copy()
        }

        val bonusDelay = 20L
        val bonusesAtOnce = 2
        fun randomBonus() = Bonus.hat

        fun randomBrickSpawn(): Vector2 {
            val ab = spawnBrickPoints[Random().nextInt(spawnBrickPoints.size)]
            return ab.copy()
        }
    }
}
