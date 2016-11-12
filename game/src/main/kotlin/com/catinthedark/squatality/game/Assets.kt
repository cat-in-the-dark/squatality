package com.catinthedark.squatality.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.catinthedark.lib.AnimationUtils

object Assets {
    fun load(): AssetManager {
        return AssetManager().apply {
            val textures = listOf("logo.png", "pairing.png", "brick.png", "gop_black.png", "gop_red.png", "fans.png", "gop_blue.png", "gopofon.png", "menu.png", "gop_green.png", "kepa.png", "title.png", "touchBackground.png", "touchKnob.png", "view_list_btn.png")
            val sounds = listOf(Names.Sound.MUSIC, Names.Sound.KILL, Names.Sound.THROW)

            load("fonts/tahoma-10.fnt", BitmapFont::class.java)
            textures.forEach { load("textures/$it", Texture::class.java) }
            sounds.forEach { load(it, Music::class.java) }
        }
    }

    object Names {
        val FONT = "fonts/tahoma-10.fnt"
        val LOGO = "textures/logo.png"
        val TITLE = "textures/title.png"
        val PAIRING = "textures/pairing.png"
        val TUTORIAL = "textures/menu.png"
        val FIELD = "textures/gopofon.png"
        val KNOB_BACKGROUND = "textures/touchBackground.png"
        val KNOB = "textures/touchKnob.png"
        val BRICK = "textures/brick.png"
        val BONUS = "textures/kepa.png"
        val FANS = "textures/fans.png"

        object Player {
            val RED = "textures/gop_red.png"
            val BLACK = "textures/gop_black.png"
            val BLUE = "textures/gop_blue.png"
        }

        object Button {
            val LIST = "textures/view_list_btn.png"
        }

        object Sound {
            val MUSIC = "sounds/bgm.mp3"
            val KILL = "sounds/head_shot.mp3"
            val THROW = "sounds/throw.mp3"

            fun throwing(am: AssetManager): Music {
                return am[THROW, Music::class.java]
            }

            fun kill(am: AssetManager): Music {
                return am[KILL, Music::class.java]
            }
        }
    }

    abstract class FanSkin(texture: Texture) {
        protected val frames: Array<Array<TextureRegion>> = TextureRegion.split(texture, 100, 100)
        protected abstract val handsUpIndexes: Array<Pair<Int, Int>>
        protected abstract val idleIndexes: Array<Pair<Int, Int>>
        val handsUp: Animation
            get() = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, *handsUpIndexes)
        val idle: Animation
            get() = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, *idleIndexes)

    }

    class BlueFanSkin(texture: Texture) : FanSkin(texture) {
        override val handsUpIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(0, 0), Pair(0, 1))
        override val idleIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(0, 0))
    }

    class RedFanSkin(texture: Texture) : FanSkin(texture) {
        override val handsUpIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(1, 0), Pair(1, 1))
        override val idleIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(1, 0))
    }

    class BlackFanSkin(texture: Texture) : FanSkin(texture) {
        override val handsUpIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(2, 0), Pair(2, 1))
        override val idleIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(2, 0))
    }

    class GirlFanSkin(texture: Texture) : FanSkin(texture) {
        override val handsUpIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(3, 0), Pair(3, 1))
        override val idleIndexes: Array<Pair<Int, Int>>
            get() = arrayOf(Pair(3, 0))
    }

    data class PlayerSkin(private val texture: Texture) {
        private val frames = TextureRegion.split(texture, 108, 108)
        val idle = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, Pair(0, 0))
        val idleWithBrick = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, Pair(0, 7))
        val running = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames,
            Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6))
        val runningWithBrick = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames,
            Pair(0, 7), Pair(0, 8), Pair(0, 9), Pair(0, 10), Pair(0, 11), Pair(0, 12), Pair(0, 13))
        val killed = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames, Pair(0, 14))
        val throwing = AnimationUtils.loopingAnimation(Const.UI.animationSpeed, frames,
            Pair(0, 15), Pair(0, 16), Pair(0, 17), Pair(0, 18))

    }
}
