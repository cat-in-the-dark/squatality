package com.catinthedark.squatality.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.catinthedark.squatality.game.SquatalityGame

class MainActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cfg = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            hideStatusBar = true
        }
        initialize(SquatalityGame(), cfg)
    }
}
