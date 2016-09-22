package com.catinthedark.squatality

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            load("textures/logo.png", Texture::class.java)
            load("font/tahoma-10.fnt", BitmapFont::class.java)
            val sounds = listOf("bgm", "chponk_suka", "head_shot", "ricochet", "run", "siklo", "stadium", "throw", "zuby_po_vsey_ulitse")
            sounds.forEach {
                load("sound/$it.mp3", Music::class.java)
            }
        }
    }
}
