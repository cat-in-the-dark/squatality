package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.PlayersListComponent
import com.catinthedark.squatality.game.services.StatsService

class UIPlayersListSystem(
    val hudStage: Stage,
    val am: AssetManager
) : IteratingSystem(
    Family.all(PlayersListComponent::class.java).get()
) {
    private var isShowing = false // actually system should never handle some global state, but we know, that there is only one players list in the game.
    private val service = StatsService(hudStage)

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        service.register()
        registerButton()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val plc = Mappers.ui.players[entity] ?: return
        service.process(plc.players(), plc.meId, isShowing)
    }

    override fun removedFromEngine(engine: Engine?) {
        service.unregister()
        super.removedFromEngine(engine)
    }

    private fun registerButton() {
        val btn = ImageButton(TextureRegionDrawable(TextureRegion(am.get(Assets.Names.Button.LIST, Texture::class.java))))
        btn.setBounds(20f, 720f - 72f - 20f, 72f, 72f) // yep, it's magic numbers. 48 is the size of texture. 72 - it's touch area. ake it bigger to work on mobile.
        btn.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isShowing = true
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isShowing = false
            }
        })
        hudStage.addActor(btn)
    }
}
