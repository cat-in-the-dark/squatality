package com.catinthedark.squatality

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

class SquatalityGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var logo: Texture

    override fun create() {
        logo = Texture("textures/logo.png")
        batch = SpriteBatch()
    }

    override fun render() {
        super.render()
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.draw(logo, 0f, 0f)
        batch.end()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        logo.dispose()
    }
}
