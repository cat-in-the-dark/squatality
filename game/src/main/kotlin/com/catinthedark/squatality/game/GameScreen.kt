package com.catinthedark.squatality.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.catinthedark.lib.YieldUnit
import com.catinthedark.squatality.game.components.*
import com.catinthedark.squatality.game.systems.*

class GameScreen(
    private val stage: Stage,
    private val hudStage: Stage
) : YieldUnit<AssetManager, Any> {
    private val engine = PooledEngine()
    private lateinit var am: AssetManager

    override fun onActivate(data: AssetManager) {
        println("GameScreen started")
        am = data
        engine.addSystem(RenderingSystem(stage, hudStage))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())
        engine.addSystem(LocalMovementSystem())
        //engine.addSystem(RandomControlSystem())
        engine.addSystem(KnobMovementSystem())
        engine.addSystem(KnobAimSystem())
        engine.addSystem(CameraSystem(stage.camera))

        val mainPlayerComponent = createPlayer(0f, 0f, Assets.PlayerSkin(am.get(Assets.Names.Player.BLUE)))
        engine.addEntity(mainPlayerComponent)
        engine.addEntity(createPlayer(200f, 200f, Assets.PlayerSkin(am.get(Assets.Names.Player.RED))))
        engine.addEntity(createPlayer(400f, 200f, Assets.PlayerSkin(am.get(Assets.Names.Player.BLACK))))
        engine.addEntity(createPlayer(600f, 600f, Assets.PlayerSkin(am.get(Assets.Names.Player.RED))))
        engine.addEntity(createField())
        engine.addEntity(createMovementKnob(15f, 15f, mainPlayerComponent.getComponent(MoveComponent::class.java)))
        engine.addEntity(createAimKnob(1015f, 15f, mainPlayerComponent.getComponent(AimComponent::class.java)))
        engine.addEntity(createCamera(mainPlayerComponent.getComponent(TransformComponent::class.java)))

        Gdx.input.inputProcessor = hudStage
    }

    override fun run(delta: Float): Any? {
        engine.update(delta)
        return null
    }

    override fun onExit() {
        engine.removeAllEntities()
        stage.dispose()
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
            val mc = engine.createComponent(MoveComponent::class.java)
            val aimC = engine.createComponent(AimComponent::class.java)

            ac.animations["IDLE"] = skin.idle
            ac.animations["RUNNING"] = skin.running
            ac.animations["KILLED"] = skin.killed
            ac.animations["THROWING"] = skin.throwing
            ac.animations["RUNNING_WITH_BRICK"] = skin.runningWithBrick
            ac.animations["IDLE_WITH_BRICK"] = skin.idleWithBrick
            sc.state = "RUNNING"

            trc.pos.x = x
            trc.pos.y = y
            mc.acceleration.x = 250f
            mc.acceleration.y = 250f

            add(ac)
            add(tc)
            add(sc)
            add(trc)
            add(mc)
            add(aimC)
        })
    }

    fun createField(): Entity {
        return engine.createEntity().apply {
            val tc = engine.createComponent(TextureComponent::class.java)
            val trc = engine.createComponent(TransformComponent::class.java)
            tc.region = TextureRegion(am.get(Assets.Names.FIELD, Texture::class.java))
            trc.pos.z = -1f
            add(tc)
            add(trc)
        }
    }

    fun createMovementKnob(x: Float, y: Float, mc: MoveComponent): Entity {
        return engine.createEntity().apply {
            val kc = engine.createComponent(KnobComponent::class.java)
            kc.touchPad = Touchpad(10f,
                Touchpad.TouchpadStyle().apply {
                    background = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB_BACKGROUND, Texture::class.java)))
                    knob = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB, Texture::class.java)))
                }
            ).apply {
                setBounds(x, y, 250f, 250f)
            }
            hudStage.addActor(kc.touchPad)

            add(kc)
            add(mc)
        }
    }

    fun createAimKnob(x: Float, y: Float, ac: AimComponent) : Entity {
        return engine.createEntity().apply {
            val kc = engine.createComponent(KnobComponent::class.java)
            kc.touchPad = Touchpad(10f,
                Touchpad.TouchpadStyle().apply {
                    background = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB_BACKGROUND, Texture::class.java)))
                    knob = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB, Texture::class.java)))
                }
            ).apply {
                setBounds(x, y, 250f, 250f)
            }
            hudStage.addActor(kc.touchPad)

            add(kc)
            add(ac)
        }
    }

    fun createCamera(target: TransformComponent): Entity {
        return engine.createEntity().apply {
            val cam = engine.createComponent(CameraComponent::class.java)
            cam.rightUpperCorner.x = 1551f
            cam.rightUpperCorner.y = 1122f
            add(cam)
            add(target)
        }
    }
}
