package com.catinthedark.squatality.game.services

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.utils.Disposable
import com.catinthedark.squatality.game.Assets
import java.util.*

class SoundService(
    private val ger: GameEventsRegistrar
) : Disposable {
    private var isRegistered: Boolean = false
    private var onThrowBrickEventId: UUID? = null
    private var onKilledEventId: UUID? = null

    var volume: Float = 1f

    fun register(am: AssetManager) {
        if (isRegistered) {
            Gdx.app.error("SoundService", "has been already registered. You will get funny errors!")
            return
        }
        isRegistered = true

        onThrowBrickEventId = ger.onThrowBrickEvent.subscribe {
            play(Assets.Names.Sound.throwing(am))
        }
        onKilledEventId = ger.onKilledEvent.subscribe {
            play(Assets.Names.Sound.kill(am))
        }
    }

    override fun dispose() {
        isRegistered = false
        ger.onThrowBrickEvent.unsubscribe(onThrowBrickEventId)
        ger.onKilledEvent.unsubscribe(onKilledEventId)
    }

    private fun play(music: Music) {
        music.volume = volume
        music.play()
    }
}
