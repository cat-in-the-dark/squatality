package com.catinthedark.squatality.game.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.NetworkControl
import com.catinthedark.squatality.game.World
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.systems.*

class GameScreen(
    private val stage: Stage,
    private val hudStage: Stage,
    private val nc: NetworkControl
) : YieldUnit<AssetManager, Any> {
    private val engine = PooledEngine()
    private lateinit var world: World

    override fun onActivate(data: AssetManager) {
        Gdx.app.log("GameScreen", "GameScreen started")
        world = World(engine, data)
        engine.addSystem(RenderingSystem(stage, hudStage))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())
        //engine.addSystem(LocalMovementSystem())
        val ls = LerpSystem()
        engine.addSystem(ls)
        engine.addSystem(RemoteMovementSystem(nc.sender))
        val rcs = RemoteControlSystem(nc.onGameState)
        engine.addSystem(rcs)
        //engine.addSystem(RandomControlSystem())
        engine.addSystem(KnobMovementSystem())
        engine.addSystem(KnobAimSystem())
        engine.addSystem(FollowCameraSystem(stage.camera))
        engine.addSystem(PerformanceSystem(hudStage, rcs.getSyncDelta, ls.getLerpDelay))

        engine.addEntity(world.createField())

        nc.onGameStarted.subscribe { gsm ->
            val mainPlayerComponent = world.createPlayer(gsm.clientId, 0f, 0f, Assets.PlayerSkin(data.get(Assets.Names.Player.BLUE)))
            engine.addEntity(mainPlayerComponent)
            engine.addEntity(world.createMovementKnob(30f, 20f, mainPlayerComponent.getComponent(MoveComponent::class.java), hudStage))
            engine.addEntity(world.createAimKnob(1000f, 20f, mainPlayerComponent.getComponent(AimComponent::class.java), hudStage))
            engine.addEntity(world.createCamera(mainPlayerComponent.getComponent(TransformComponent::class.java)))
            gsm.enemies.forEach { enemyId ->
                val enemy = world.createUnit(enemyId, 0f, 0f, Assets.PlayerSkin(data.get(Assets.Names.Player.RED)))
                engine.addEntity(enemy)
            }
        }

        nc.onEnemyConnected.subscribe {
            val enemy = world.createUnit(it.clientId, 0f, 0f, Assets.PlayerSkin(data.get(Assets.Names.Player.RED)))
            engine.addEntity(enemy)
        }

        Gdx.input.inputProcessor = hudStage
    }

    override fun run(delta: Float): Any? {
        engine.update(delta)
        return null
    }

    override fun onExit() {
        engine.removeAllEntities()
        stage.dispose()
        nc.dispose()
    }
}
