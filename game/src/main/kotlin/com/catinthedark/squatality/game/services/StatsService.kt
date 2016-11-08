package com.catinthedark.squatality.game.services

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.catinthedark.squatality.models.ShortPlayerModel
import java.util.*

class StatsService(
    val stage: Stage
) {
    private val pixel = Pixmap(1, 1, Pixmap.Format.Alpha).apply {
        setColor(0f, 0f, 0f, 0.5f) // black with alpha
        fill()
    }
    private val bg = TextureRegionDrawable(TextureRegion(Texture(pixel)))
    private val font = BitmapFont()
    private val style = Label.LabelStyle(font, Color.WHITE)
    private val styleMe = Label.LabelStyle(font, Color.GOLD)
    private val styleOffline = Label.LabelStyle(font, Color.RED)
    private val table = Table().apply {
        background = bg
    }

    fun register() {
        stage.addActor(table)
    }

    fun process(players: List<ShortPlayerModel>, meId: UUID?, isShowing: Boolean = true) {
        table.reset()

        if (isShowing) {
            table.add(l("Name")).padLeft(10f)
            table.add(l("Deaths")).padLeft(20f)
            table.add(l("Frags")).padLeft(20f).padRight(10f)
            table.row()
            players.forEach { player ->
                row(table, player, meId)
            }
        }

        table.pack()
        table.left()
        table.top()
        table.x = 300f
        table.y = 720f - 120f - table.height
    }

    fun unregister() {
        table.remove()
    }

    private fun row(table: Table, player: ShortPlayerModel, meId: UUID?) {
        val s = if (player.id == meId) {
            styleMe
        } else if (player.isOnline) {
            style
        } else {
            styleOffline
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
