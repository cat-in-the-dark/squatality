package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.PlayersListComponent

class UIPlayersListSystem(
    val hudStage: Stage,
    val am: AssetManager
) : IteratingSystem(
    Family.all(PlayersListComponent::class.java).get()
) {
    private val table = Table()
    private val font = BitmapFont()
    private val style = Label.LabelStyle(font, Color.WHITE)
    private var isShowing = false // actually system should never handle some global state, but we know, that there is only one players list in the game.

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        hudStage.addActor(table)

        val btn = ImageButton(TextureRegionDrawable(TextureRegion(am.get(Assets.Names.Button.LIST, Texture::class.java))))
        btn.setPosition(20f, 720f - btn.height - 20f)
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

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        table.reset()
        table.left()
        table.top()
        table.x = 100f
        table.y = 700f

        val plc = Mappers.ui.players[entity] ?: return
        if (isShowing) {
            plc.players.values.forEach { player ->
                table.add(l(player.name))
                table.add(l(player.deaths.toString())).padLeft(10f)
                table.add(l(player.frags.toString())).padLeft(10f)
                table.row()
            }
        }
    }

    override fun removedFromEngine(engine: Engine?) {
        table.remove()
        super.removedFromEngine(engine)
    }

    private fun l(text: String): Label {
        return Label(text, style).apply {
            setFontScale(2f, 2f)
        }
    }
}
