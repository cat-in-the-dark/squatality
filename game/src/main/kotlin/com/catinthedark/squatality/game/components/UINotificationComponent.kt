package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class UINotificationComponent(
    val list: MutableList<Notification> = arrayListOf()
):Pool.Poolable, Component {
    override fun reset() {
        list.clear()
    }
}

data class Notification(
    val text: String,
    var ttl: Int = 20
)
