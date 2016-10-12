package com.catinthedark.squatality.game.components.network

import com.catinthedark.squatality.models.BrickModel
import java.util.*

data class BricksComponent(
    override val queue: Queue<Pair<List<BrickModel>, Long>> = LinkedList()
): NetworkComponent<BrickModel>
