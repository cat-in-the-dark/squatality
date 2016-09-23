package com.catinthedark.squatality.models

import java.io.Serializable
import java.util.*

interface Model : Serializable

data class GameStateModel(
    val me: PlayerModel,
    val players: List<PlayerModel>,
    val bricks: List<BrickModel>,
    val bonuses: List<BonusModel>,
    val time: Long) : Model

data class PlayerModel(
    val id: UUID,
    val name: String,
    var x: Float,
    var y: Float,
    var angle: Float,
    var state: State,
    var skin: String,
    val bonuses: MutableList<String> = arrayListOf(),
    var frags: Int = 0,
    var deaths: Int = 0,
    var hasBrick: Boolean = false) : Model

data class BrickModel(
    val id: UUID,
    var x: Float,
    var y: Float,
    var angle: Double,
    var hurting: Boolean) : Model

data class BonusModel(
    val id: UUID,
    val x: Float,
    val y: Float,
    val typeName: String) : Model
