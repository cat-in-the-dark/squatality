package com.catinthedark.squatality.game.screens.messages

import com.badlogic.gdx.assets.AssetManager
import com.catinthedark.squatality.models.RoomStatisticsModel

data class StatsMessage(
    val am: AssetManager,
    val stats: RoomStatisticsModel
)
