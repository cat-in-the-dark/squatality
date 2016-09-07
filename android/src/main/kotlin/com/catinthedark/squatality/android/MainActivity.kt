package com.catinthedark.squatality.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.catinthedark.squatality.SquatalityGame

class MainActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cfg = AndroidApplicationConfiguration()
        initialize(SquatalityGame(), cfg)
    }
}
