package com.catinthedark.squatality.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion

data class TextureComponent(
    var region: TextureRegion? = null
): Component
