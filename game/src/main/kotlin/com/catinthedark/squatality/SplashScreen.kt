package com.catinthedark.squatality

import com.badlogic.gdx.assets.AssetManager
import com.catinthedark.lib.YieldUnit

class SplashScreen: YieldUnit<Unit, AssetManager> {
    lateinit var am: AssetManager

    override fun onActivate(data: Unit) {
        am = Assets.load()
    }

    override fun run(delta: Float): AssetManager? {
        if (am.update()) {
            return am
        }
        println("Loading assets...")
        return null
    }

    override fun onExit() {

    }
}
