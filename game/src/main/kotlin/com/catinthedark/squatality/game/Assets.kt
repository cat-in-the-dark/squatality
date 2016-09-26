package com.catinthedark.squatality.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.catinthedark.lib.AnimationUtils

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            val textures = listOf("logo.png", "brick.png", "gop_black.png", "gop_red.png", "fans.png", "gop_blue.png", "gopofon.png", "menu.png", "gop_green.png", "kepa.png", "title.png", "touchBackground.png", "touchKnob.png")
            val sounds = listOf("bgm.mp3", "chponk_suka.mp3", "head_shot.mp3", "ricochet.mp3", "run.mp3", "siklo.mp3", "stadium.mp3", "throw.mp3", "zuby_po_vsey_ulitse.mp3")

            load("fonts/tahoma-10.fnt", BitmapFont::class.java)
            textures.forEach { load("textures/$it", Texture::class.java) }
            sounds.forEach { load("sounds/$it", Music::class.java) }
        }
    }

    object Names {
        val FONT = "fonts/tahoma-10.fnt"
        val LOGO = "textures/logo.png"
        val TITLE = "textures/title.png"
        val TUTORIAL = "textures/menu.png"
        val FIELD = "textures/gopofon.png"
        val KNOB_BACKGROUND = "textures/touchBackground.png"
        val KNOB = "textures/touchKnob.png"
        object Player {
            val RED = "textures/gop_red.png"
            val BLACK = "textures/gop_black.png"
            val BLUE = "textures/gop_blue.png"
        }
    }

    data class PlayerSkin(private val texture: Texture) {
        private val frames = TextureRegion.split(texture, 108, 108)
        val idle = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, Pair(0,0))
        val idleWithBrick = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, Pair(0, 7))
        val running = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames,
            Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6))
        val runningWithBrick = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames,
            Pair(0, 7), Pair(0, 8), Pair(0, 9), Pair(0, 10), Pair(0, 11), Pair(0, 12), Pair(0, 13))
        val killed = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, Pair(0,14))
        val throwing = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames,
            Pair(0, 15), Pair(0, 16), Pair(0, 17), Pair(0, 18))

    }
}