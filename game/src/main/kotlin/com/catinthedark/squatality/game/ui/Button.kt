package com.catinthedark.squatality.game.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.Observable

class Button(
    val background: TextureRegion,
    val rect: Rectangle
) {
    val onClicked = Observable<Vector2>()

    fun onClick(clickPos: Vector2): Boolean {
        return if (rect.contains(clickPos)) {
            onClicked(clickPos)
            true
        } else {
            false
        }
    }

    fun dispose() {
        onClicked.clear()
    }
}
