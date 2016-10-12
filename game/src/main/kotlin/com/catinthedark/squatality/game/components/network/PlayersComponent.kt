package com.catinthedark.squatality.game.components.network

import com.catinthedark.squatality.models.PlayerModel
import java.util.*

data class PlayersComponent(
    override val queue: Queue<Pair<List<PlayerModel>, Long>> = LinkedList()
) : NetworkComponent<PlayerModel>
