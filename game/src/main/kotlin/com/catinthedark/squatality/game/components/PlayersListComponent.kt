package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import java.util.*

data class PlayersListComponent(
    val players: MutableMap<UUID, PlayerShortModel> = hashMapOf()
) : Component, Pool.Poolable {
    override fun reset() {
        players.clear()
    }
}

data class PlayerShortModel(
    val id: UUID,
    val name: String,
    var skin: String = "",
    val bonuses: List<String> = listOf(),
    var frags: Int = 0,
    var deaths: Int = 0,
    var hasBrick: Boolean = false)
