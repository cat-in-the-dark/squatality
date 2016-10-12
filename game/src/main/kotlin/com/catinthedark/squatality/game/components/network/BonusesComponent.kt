package com.catinthedark.squatality.game.components.network

import com.catinthedark.squatality.models.BonusModel
import java.util.*

data class BonusesComponent(
    override val queue: Queue<Pair<List<BonusModel>, Long>> = LinkedList()
) : NetworkComponent<BonusModel>
