package com.catinthedark.squatality

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            val textures = listOf("logo.png", "brick.png", "gop_black.png", "gop_red.png", "fans.png", "gop_blue.png", "gopofon.png", "menu.png", "field.png", "gop_green.png", "kepa.png", "title.png")
            val sounds = listOf("bgm.mp3", "chponk_suka.mp3", "head_shot.mp3", "ricochet.mp3", "run.mp3", "siklo.mp3", "stadium.mp3", "throw.mp3", "zuby_po_vsey_ulitse.mp3")

            load("fonts/tahoma-10.fnt", BitmapFont::class.java)
            textures.forEach { load("textures/$it", Texture::class.java) }
            sounds.forEach { load("sounds/$it", Music::class.java) }
        }
    }

    object Names {
        val LOGO = "textures/logo.png"
    }
}
