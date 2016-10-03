package com.catinthedark.squatality.android

import android.os.Bundle
import android.os.StrictMode
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.catinthedark.squatality.game.SquatalityGame

class MainActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        StrictMode
            .setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectCustomSlowCalls()
                .detectNetwork()
                .penaltyLog()
                .build())
        StrictMode
            .setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectCleartextNetwork()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())
        super.onCreate(savedInstanceState)
        val cfg = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            hideStatusBar = true
        }
        initialize(SquatalityGame(), cfg)
    }
}
