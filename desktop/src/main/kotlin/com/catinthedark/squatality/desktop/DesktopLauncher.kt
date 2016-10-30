package com.catinthedark.squatality.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.catinthedark.lib.network.ConnectionOptions
import com.catinthedark.squatality.game.Const
import com.catinthedark.squatality.game.SquatalityGame

object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true")

        val address = if (args.getOrNull(0) != null) {
            ConnectionOptions(null, args[0], 54555, 54777)
        } else {
            Const.Network.server
        }

        LwjglApplication(SquatalityGame(address), LwjglApplicationConfiguration().apply {
            title = "Squatality"
            width = 1161
            height = 652
        })
    }
}
