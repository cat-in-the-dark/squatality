package com.catinthedark.lib

import com.badlogic.gdx.graphics.g2d.SpriteBatch

fun SpriteBatch.managed(block: (batch: SpriteBatch) -> Unit) {
    begin()
    block(this)
    end()
}
