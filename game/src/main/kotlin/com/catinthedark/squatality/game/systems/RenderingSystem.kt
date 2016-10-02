package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
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
    override fun processEntity(entity: Entity?, deltaTime: Float) {
        stage.batch.managed { b ->
            val tex = Mappers.texture[entity]
            val t = Mappers.transform[entity]
            val region = tex?.region
            if (region != null && t != null) {
                var x = t.pos.x
                var y = t.pos.y
                if (tex.centered) {
                    x = t.pos.x - region.regionWidth.toFloat() / 2
                    y = t.pos.y - region.regionHeight.toFloat() / 2
                }
                b.draw(
                    region,
                    x, y,
                    region.regionWidth.toFloat() / 2, region.regionHeight.toFloat() / 2,
                    region.regionWidth.toFloat(), region.regionHeight.toFloat(),
                    1f, 1f,
                    t.angle
                )
            }
        }
        stage.draw()
        hudStage.draw()
    }

}
