package com.catinthedark.squatality

import com.badlogic.gdx.assets.AssetManager
import com.catinthedark.lib.YieldUnit

class GameScreen: YieldUnit<AssetManager, Any> {
    override fun onActivate(data: AssetManager) {
        println("GameScreen started")
    }

    override fun run(delta: Float): Any? {
        return null
    }

    override fun onExit() {

    }
}
