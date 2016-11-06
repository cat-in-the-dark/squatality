package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
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
import com.catinthedark.squatality.game.components.PlayerShortModel
import com.catinthedark.squatality.game.components.PlayersListComponent

class UIPlayersListSystem(
    val hudStage: Stage,
    val am: AssetManager
) : IteratingSystem(
    Family.all(PlayersListComponent::class.java).get()
) {
    private val table = Table()
    private val pixel = Pixmap(1, 1, Pixmap.Format.Alpha).apply {
        setColor(0f, 0f, 0f, 0.5f) // black with alpha
        fill()
    }
    private val background = TextureRegionDrawable(TextureRegion(Texture(pixel)))
    private val font = BitmapFont()
    private val style = Label.LabelStyle(font, Color.WHITE)
    private val styleMe = Label.LabelStyle(font, Color.GOLD)
    private var isShowing = false // actually system should never handle some global state, but we know, that there is only one players list in the game.

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        hudStage.addActor(table)
        table.background = background

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

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        table.reset()
        table.left()
        table.top()
        table.x = 300f
        table.y = 600f

        val plc = Mappers.ui.players[entity] ?: return
        if (isShowing) {
            table.add(l("Name")).padLeft(10f)
            table.add(l("Deaths")).padLeft(20f)
            table.add(l("Frags")).padLeft(20f).padRight(10f)
            table.row()
            plc.players().forEach { player ->
                row(table, player, plc)
            }
        }

        table.pack()
    }

    override fun removedFromEngine(engine: Engine?) {
        table.remove()
        super.removedFromEngine(engine)
    }

    private fun row(table: Table, player: PlayerShortModel, plc: PlayersListComponent) {
        val s = if (player.id == plc.meId) {
            styleMe
        } else {
            style
        }
        table.add(l(player.name, s)).padLeft(10f)
        table.add(l(player.deaths.toString(), s)).padLeft(20f)
        table.add(l(player.frags.toString(), s)).padLeft(20f).padRight(10f)
        table.row()
    }

    private fun l(text: String, s: Label.LabelStyle = style): Label {
        return Label(text, s).apply {
            setFontScale(2f, 2f)
        }
    }
}
