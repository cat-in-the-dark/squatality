package com.catinthedark.squatality.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.catinthedark.squatality.game.components.*
import java.util.*

class World(
    private val engine: Engine,
    private val am: AssetManager
) {
    fun createHelp(): Entity {
        return with(engine.createEntity(), {
            val texComp = engine.createComponent(TextureComponent::class.java)
            texComp.region = com.badlogic.gdx.graphics.g2d.TextureRegion(am.get(com.catinthedark.squatality.game.Assets.Names.TUTORIAL, Texture::class.java))

            add(texComp)
            add(engine.createComponent(TransformComponent::class.java))
        })
    }

    fun createPlayer(id: UUID, x: Float = 0f, y: Float = 0f, skin: Assets.PlayerSkin): Entity {
        return with(engine.createEntity(), {
            val ac = engine.createComponent(AnimationComponent::class.java)
            val tc = engine.createComponent(TextureComponent::class.java)
            val sc = engine.createComponent(StateComponent::class.java)
            val trc = engine.createComponent(TransformComponent::class.java)
            val mc = engine.createComponent(MoveComponent::class.java)
            val aimC = engine.createComponent(AimComponent::class.java)
            val rmc = engine.createComponent(RemoteMoveComponent::class.java)
            val ric = engine.createComponent(RemoteIDComponent::class.java)

            ric.id = id
            ac.animations["IDLE"] = skin.idle
            ac.animations["RUNNING"] = skin.running
            ac.animations["KILLED"] = skin.killed
            ac.animations["THROWING"] = skin.throwing
            ac.animations["RUNNING_WITH_BRICK"] = skin.runningWithBrick
            ac.animations["IDLE_WITH_BRICK"] = skin.idleWithBrick
            sc.state = "IDLE"

            tc.centered = true
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
            add(rmc)
            add(ric)
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

    fun createMovementKnob(x: Float, y: Float, mc: MoveComponent, hudStage: Stage): Entity {
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

    fun createAimKnob(x: Float, y: Float, ac: AimComponent, hudStage: Stage): Entity {
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
