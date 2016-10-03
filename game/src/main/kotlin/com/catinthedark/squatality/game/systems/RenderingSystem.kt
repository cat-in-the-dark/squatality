package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.TextureComponent
import com.catinthedark.squatality.game.components.TransformComponent
import java.util.*

class RenderingSystem(
    private val stage: Stage,
    private val hudStage: Stage
) : SortedIteratingSystem(
    Family.all(TextureComponent::class.java, TransformComponent::class.java).get(),
    Comparator<Entity> { a, b ->
        Math.signum(Mappers.transform[a].pos.z - Mappers.transform[b].pos.z).toInt()
    }
) {
    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        hudStage.draw()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        stage.batch.managed { b ->
            val tex = Mappers.texture[entity] ?: return@managed
            val t = Mappers.transform[entity] ?: return@managed
            val region = tex.region ?: return@managed

            val pos = if (tex.centered) {
                Vector2(
                    t.pos.x - region.regionWidth.toFloat() / 2,
                    t.pos.y - region.regionHeight.toFloat() / 2)
            } else {
                Vector2(t.pos.x, t.pos.y)
            }
            b.draw(
                region,
                pos.x, pos.y,
                region.regionWidth.toFloat() / 2, region.regionHeight.toFloat() / 2,
                region.regionWidth.toFloat(), region.regionHeight.toFloat(),
                1f, 1f,
                t.angle
            )
        }
        stage.draw()
    }

}
