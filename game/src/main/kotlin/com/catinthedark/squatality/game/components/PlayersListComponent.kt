package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import java.util.*

data class PlayersListComponent(
    val players: MutableMap<UUID, PlayerShortModel> = hashMapOf(),
    var meId: UUID? = null
) : Component, Pool.Poolable {
    fun me(): PlayerShortModel? = players[meId]

    fun enemies(): List<PlayerShortModel> = players.filter { it.key != meId }.values.toList()

    fun players(): List<PlayerShortModel> = players.values.sortedByDescending { it.frags }

    override fun reset() {
        players.clear()
        meId = null
    }
}

data class PlayerShortModel(
    val id: UUID,
    val name: String,
    var skin: String = "",
    val bonuses: List<String> = listOf(),
    var frags: Int = 0,
    var deaths: Int = 0,
    var hasBrick: Boolean = false,
    var isOnline: Boolean = true)
