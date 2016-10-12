package com.catinthedark.squatality.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.catinthedark.lib.ashley.createComponent
import com.catinthedark.squatality.game.components.*
import com.catinthedark.squatality.game.components.network.BonusesComponent
import com.catinthedark.squatality.game.components.network.BricksComponent
import com.catinthedark.squatality.game.components.network.PlayersComponent
import com.catinthedark.squatality.models.BonusModel
import com.catinthedark.squatality.models.BrickModel
import com.catinthedark.squatality.models.State
import java.util.*

class World(
    private val engine: Engine,
    private val am: AssetManager
) {
    /**
     * General unit. Can be either enemy or player.
     */
    fun createUnit(id: UUID, x: Float = 0f, y: Float = 0f, skin: Assets.PlayerSkin): Entity {
        return with(engine.createEntity(), {
            val ac = engine.createComponent(AnimationComponent::class.java)
            val tc = engine.createComponent(TextureComponent::class.java)
            val sc = engine.createComponent(StateComponent::class.java)
            val trc = engine.createComponent(TransformComponent::class.java)
            val ric = engine.createComponent(RemoteIDComponent::class.java)
            val ltc = engine.createComponent(LerpTransformComponent::class.java)

            ric.id = id
            ac.animations[State.IDLE.name] = skin.idle
            ac.animations[State.RUNNING.name] = skin.running
            ac.animations[State.KILLED.name] = skin.killed
            ac.animations[State.THROWING.name] = skin.throwing
            ac.animations["RUNNING_WITH_BRICK"] = skin.runningWithBrick
            ac.animations["IDLE_WITH_BRICK"] = skin.idleWithBrick
            sc.state = State.IDLE.name

            tc.centered = true
            trc.pos.x = x
            trc.pos.y = y
            trc.pos.z = 1f

            add(ac)
            add(tc)
            add(sc)
            add(trc)
            add(ric)
            add(ltc)
        })
    }

    /**
     * Controllable unit. It's player.
     */
    fun createPlayer(id: UUID, x: Float = 0f, y: Float = 0f, skin: Assets.PlayerSkin): Entity {
        return with(createUnit(id, x, y, skin), {
            val mc = engine.createComponent(MoveComponent::class.java)
            val aimC = engine.createComponent(AimComponent::class.java)
            val rmc = engine.createComponent(RemoteMoveComponent::class.java)

            mc.acceleration.x = 250f
            mc.acceleration.y = 250f

            add(mc)
            add(aimC)
            add(rmc)
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

    fun createSync(): Entity {
        return engine.createEntity().apply {
            val brc = engine.createComponent(BricksComponent::class.java)
            val bc = engine.createComponent(BonusesComponent::class.java)
            val pc = engine.createComponent(PlayersComponent::class.java)
            add(brc)
            add(bc)
            add(pc)
        }
    }

    fun createBrick(brick: BrickModel): Entity {
        return engine.createEntity().apply {
            val trc: TransformComponent = engine.createComponent()
            val tc: TextureComponent = engine.createComponent()
            val ric: RemoteIDComponent = engine.createComponent()
            val hc: HurtComponent = engine.createComponent()

            tc.region = TextureRegion(am.get(Assets.Names.BRICK, Texture::class.java))
            tc.centered = true
            ric.id = brick.id
            trc.pos.x = brick.x
            trc.pos.y = brick.y
            trc.angle = brick.angle.toFloat()
            hc.hurting = brick.hurting

            add(tc)
            add(trc)
            add(ric)
            add(hc)
            println("NEW BRICK $brick")
        }
    }
}
