package com.catinthedark.squatality.game

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.catinthedark.lib.ashley.createComponent
import com.catinthedark.squatality.Const
import com.catinthedark.squatality.game.components.*
import com.catinthedark.squatality.game.components.network.BonusesComponent
import com.catinthedark.squatality.game.components.network.BricksComponent
import com.catinthedark.squatality.game.components.network.PlayersComponent
import com.catinthedark.squatality.models.BonusModel
import com.catinthedark.squatality.models.BrickModel
import com.catinthedark.squatality.models.State
import java.util.*

class World(
    val engine: Engine,
    val am: AssetManager
) {
    /**
     * General unit. Can be either enemy or player.
     */
    fun createUnit(id: UUID, x: Float = 0f, y: Float = 0f, skin: Assets.PlayerSkin): Entity {
        return engine.createEntity().apply {
            val ac: AnimationComponent = engine.createComponent()
            val tc: TextureComponent = engine.createComponent()
            val sc: StateComponent = engine.createComponent()
            val trc: TransformComponent = engine.createComponent()
            val ric: RemoteIDComponent = engine.createComponent()
            val ltc: LerpTransformComponent = engine.createComponent()

            ric.id = id
            ac.animations[State.IDLE.name] = skin.idle
            ac.animations[State.RUNNING.name] = skin.running
            ac.animations[State.KILLED.name] = skin.killed
            ac.animations[State.THROWING.name] = skin.throwing
            ac.animations["${State.RUNNING.name}_WITH_BRICK"] = skin.runningWithBrick
            ac.animations["${State.IDLE.name}_WITH_BRICK"] = skin.idleWithBrick
            ac.animations["${State.KILLED.name}_WITH_BRICK"] = skin.killed
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
        }
    }

    /**
     * Controllable unit. It's player.
     */
    fun createPlayer(id: UUID, x: Float = 0f, y: Float = 0f, skin: Assets.PlayerSkin): Entity {
        return createUnit(id, x, y, skin).apply {
            val mc: MoveComponent = engine.createComponent()
            val aimC: AimComponent = engine.createComponent()
            val rmc: RemoteMoveComponent = engine.createComponent()

            mc.acceleration.x = Const.Balance.playerSpeed.x
            mc.acceleration.y = Const.Balance.playerSpeed.y

            add(mc)
            add(aimC)
            add(rmc)
        }
    }

    fun createHat(followToTrc: TransformComponent, targetState: StateComponent): Entity {
        return engine.createEntity().apply {
            val tc: TextureComponent = engine.createComponent()
            val trc: TransformComponent = engine.createComponent()
            val ftrc: FollowingTransformComponent = engine.createComponent()
            ftrc.target = followToTrc
            ftrc.strategy = { from, to ->
                if (from != null && targetState.bonuses.contains(Const.Bonus.hat)) {
                    to.pos.x = from.pos.x
                    to.pos.y = from.pos.y
                    to.pos.z = from.pos.z + 1
                    to.angle = from.angle
                } else {
                    // is it ok?
                    to.pos.x = -10000f
                    to.pos.y = -10000f
                    to.pos.z = -1000f
                }
            }
            tc.centered = true
            tc.region = TextureRegion(am.get(Assets.Names.BONUS, Texture::class.java))

            add(tc)
            add(ftrc)
            add(trc)
        }
    }

    fun createFan(x: Float, y: Float, angle: Float, skin: Assets.FanSkin): Entity {
        return engine.createEntity().apply {
            val ac: AnimationComponent = engine.createComponent()
            val trc: TransformComponent = engine.createComponent()
            val tc: TextureComponent = engine.createComponent()
            val sc: StateComponent = engine.createComponent()
            sc.state = "HANDS_UP"
            ac.animations["HANDS_UP"] = skin.handsUp
            ac.animations["IDLE"] = skin.idle
            trc.pos.x = x
            trc.pos.y = y
            trc.angle = angle
            add(ac)
            add(trc)
            add(tc)
            add(sc)
        }
    }

    fun createFans(): List<Entity> {
        return listOf(
            createFan(20f, 20f, 0f, Assets.GirlFanSkin(am[Assets.Names.FANS]))
        )
    }

    fun createField(): Entity {
        return engine.createEntity().apply {
            val tc: TextureComponent = engine.createComponent()
            val trc: TransformComponent = engine.createComponent()
            tc.region = TextureRegion(am.get(Assets.Names.FIELD, Texture::class.java))
            trc.pos.z = -1f
            add(tc)
            add(trc)
        }
    }

    fun createMovementKnob(x: Float, y: Float, mc: MoveComponent, hudStage: Stage): Entity {
        return engine.createEntity().apply {
            val kc: KnobComponent = engine.createComponent()
            kc.touchPad = Touchpad(10f,
                Touchpad.TouchpadStyle().apply {
                    background = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB_BACKGROUND, Texture::class.java)))
                    knob = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB, Texture::class.java)))
                }
            ).apply {
                setBounds(x, y, 250f, 250f)
            }

            add(kc)
            add(mc)
        }
    }

    fun createAimKnob(x: Float, y: Float, ac: AimComponent, sc: StateComponent, tc: TransformComponent, hudStage: Stage): Entity {
        return engine.createEntity().apply {
            val kc: KnobComponent = engine.createComponent()
            kc.touchPad = Touchpad(10f,
                Touchpad.TouchpadStyle().apply {
                    background = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB_BACKGROUND, Texture::class.java)))
                    knob = TextureRegionDrawable(TextureRegion(am.get(Assets.Names.KNOB, Texture::class.java)))
                }
            ).apply {
                setBounds(x, y, 250f, 250f)
            }

            add(kc)
            add(ac)
            add(sc)
            add(tc)
        }
    }

    fun createCamera(target: TransformComponent): Entity {
        return engine.createEntity().apply {
            val cam: CameraComponent = engine.createComponent()
            cam.rightUpperCorner.x = 1551f
            cam.rightUpperCorner.y = 1122f
            add(cam)
            add(target)
        }
    }

    fun createSync(plc: PlayersListComponent): Entity {
        return engine.createEntity().apply {
            val brc: BricksComponent = engine.createComponent()
            val bc: BonusesComponent = engine.createComponent()
            val pc: PlayersComponent = engine.createComponent()
            val cc: ClockComponent = engine.createComponent()
            add(brc)
            add(bc)
            add(pc)
            add(cc)
            add(plc)
        }
    }

    fun createBrick(brick: BrickModel): Entity {
        return engine.createEntity().apply {
            val trc: TransformComponent = engine.createComponent()
            val ltc: LerpTransformComponent = engine.createComponent()
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
            add(ltc)
        }
    }

    fun createBonus(bonus: BonusModel): Entity {
        return engine.createEntity().apply {
            val trc: TransformComponent = engine.createComponent()
            val tc: TextureComponent = engine.createComponent()
            val ric: RemoteIDComponent = engine.createComponent()
            val bc: BonusComponent = engine.createComponent()

            tc.region = TextureRegion(am.get(Assets.Names.BONUS, Texture::class.java))
            tc.centered = true
            ric.id = bonus.id
            trc.pos.x = bonus.x
            trc.pos.y = bonus.y
            bc.typeName = bonus.typeName

            add(tc)
            add(trc)
            add(ric)
            add(bc)
        }
    }

    fun createNotifications(): Entity {
        return engine.createEntity().apply {
            val nc: UINotificationComponent = engine.createComponent()

            add(nc)
        }
    }
}
