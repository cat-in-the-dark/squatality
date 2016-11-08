package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.catinthedark.squatality.models.ShortPlayerModel
import java.util.*

data class PlayersListComponent(
    val players: MutableMap<UUID, ShortPlayerModel> = hashMapOf(),
    var meId: UUID? = null
) : Component, Pool.Poolable {
    fun me(): ShortPlayerModel? = players[meId]

    fun enemies(): List<ShortPlayerModel> = players.filter { it.key != meId }.values.toList()

    fun players(): List<ShortPlayerModel> = players.values.sortedByDescending { it.frags }

    override fun reset() {
        players.clear()
        meId = null
    }
}
