package com.catinthedark.squatality.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.catinthedark.squatality.game.Const
import com.catinthedark.squatality.game.SquatalityGame

object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true")

        LwjglApplication(SquatalityGame(Const.Network.localServer), LwjglApplicationConfiguration().apply {
            title = "Squatality"
            width = 1161
            height = 652
        })
    }
}
