package com.catinthedark.squatality.models

import java.io.Serializable
import java.util.*

interface Model : Serializable

data class GameStateModel(
    val players: List<PlayerModel> = emptyList(),
    val bricks: List<BrickModel> = emptyList(),
    val bonuses: List<BonusModel> = emptyList(),
    val time: Long = 0) : Model

data class PlayerModel(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    var updated: Boolean = true,
    var previousX: Float = 0f,
    var previousY: Float = 0f,
    var x: Float = 0f,
    var y: Float = 0f,
    var angle: Float = 0f,
    var state: State = State.IDLE,
    var skin: String = "",
    val bonuses: MutableList<String> = arrayListOf(),
    var frags: Int = 0,
    var deaths: Int = 0,
    var hasBrick: Boolean = false) : Model

data class BrickModel(
    val id: UUID = UUID.randomUUID(),
    var x: Float = 0f,
    var y: Float = 0f,
    var previousX: Float = 0f,
    var previousY: Float = 0f,
    var angle: Double = 0.0,
    var hurting: Boolean = false) : Model

data class BonusModel(
    val id: UUID = UUID.randomUUID(),
    val x: Float = 0f,
    val y: Float = 0f,
    val typeName: String = "") : Model
