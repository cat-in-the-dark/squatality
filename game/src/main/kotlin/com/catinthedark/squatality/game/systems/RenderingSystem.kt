package com.catinthedark.squatality.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.managed
import com.catinthedark.squatality.game.Mappers
import com.catinthedark.squatality.game.components.TextureComponent
import com.catinthedark.squatality.game.components.TransformComponent
import java.util.*

class RenderingSystem(
    private val batch: SpriteBatch
) : IteratingSystem(
    Family.all(*arrayOf(TextureComponent::class.java, TransformComponent::class.java)).get()
) {
    private val renderQueue = arrayListOf<Entity>()

    private val comparator = Comparator<Entity> { a, b ->
        Math.signum(Mappers.transform.get(a).pos.z - Mappers.transform.get(b).pos.z).toInt()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        batch.managed { b ->
            renderQueue.sortedWith(comparator).forEach { entity ->
                val tex = Mappers.texture.get(entity)
                val t = Mappers.transform.get(entity)
                if (tex != null && t != null && tex.region != null) {
                    b.draw(tex.region, t.pos.x, t.pos.y)
                }
            }
        }
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        if (entity != null) {
            renderQueue.add(entity)
        }
    }

}
