package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

data class TextureComponent(
    var region: TextureRegion? = null,
    var centered: Boolean = false
): Component, Pool.Poolable {
    override fun reset() {
        region = null
        centered = false
    }
}
