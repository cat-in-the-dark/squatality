package com.catinthedark.squatality.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.catinthedark.lib.YieldUnit
import com.catinthedark.squatality.game.components.AnimationComponent
import com.catinthedark.squatality.game.components.StateComponent
import com.catinthedark.squatality.game.components.TextureComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.systems.AnimationSystem
import com.catinthedark.squatality.game.systems.RenderingSystem
import com.catinthedark.squatality.game.systems.StateSystem

class GameScreen(
    private val batch: SpriteBatch
): YieldUnit<AssetManager, Any> {
    private val engine = PooledEngine()
    lateinit private var am: AssetManager

    override fun onActivate(data: AssetManager) {
        println("GameScreen started")
        am = data
        engine.addSystem(RenderingSystem(batch))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())

        engine.addEntity(createPlayer(0f,0f, Assets.PlayerSkin(am.get(Assets.Names.Player.BLUE))))
        engine.addEntity(createPlayer(200f,200f, Assets.PlayerSkin(am.get(Assets.Names.Player.RED))))
        engine.addEntity(createPlayer(400f,200f, Assets.PlayerSkin(am.get(Assets.Names.Player.BLACK))))
        engine.addEntity(createPlayer(600f,600f, Assets.PlayerSkin(am.get(Assets.Names.Player.RED))))
    }

    override fun run(delta: Float): Any? {
        engine.update(delta)
        return null
    }

    override fun onExit() {
        engine.removeAllEntities()
    }

    fun createHelp(): Entity {
        return with(engine.createEntity(), {
            val texComp = engine.createComponent(TextureComponent::class.java)
            texComp.region = TextureRegion(am.get(Assets.Names.TUTORIAL, Texture::class.java))

            add(texComp)
            add(engine.createComponent(TransformComponent::class.java))
        })
    }

    fun createPlayer(x: Float = 0f, y: Float = 0f, skin: Assets.PlayerSkin): Entity {
        return with(engine.createEntity(), {
            val ac = engine.createComponent(AnimationComponent::class.java)
            val tc = engine.createComponent(TextureComponent::class.java)
            val sc = engine.createComponent(StateComponent::class.java)
            val trc = engine.createComponent(TransformComponent::class.java)

            ac.animations["IDLE"] = skin.idle
            ac.animations["RUNNING"] = skin.running
            ac.animations["KILLED"] = skin.killed
            ac.animations["THROWING"] = skin.throwing
            ac.animations["RUNNING_WITH_BRICK"] = skin.runningWithBrick
            ac.animations["IDLE_WITH_BRICK"] = skin.idleWithBrick
            sc.state = "RUNNING"

            trc.pos.x = x
            trc.pos.y = y

            add(ac)
            add(tc)
            add(sc)
            add(trc)
        })
    }
}
