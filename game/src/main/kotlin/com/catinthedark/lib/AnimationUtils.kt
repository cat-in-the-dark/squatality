package com.catinthedark.lib

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

object AnimationUtils {
    fun loopingAnimation(speed: Float, frames: Array<Array<TextureRegion>>, vararg frameIndexes: Pair<Int, Int>): Animation {
        val array = com.badlogic.gdx.utils.Array<TextureRegion>()
        frameIndexes.forEach {
            array.add(frames[it.first][it.second])
        }
        return Animation(speed, array, Animation.PlayMode.LOOP)
    }
}
