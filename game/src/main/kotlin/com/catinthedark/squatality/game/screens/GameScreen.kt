package com.catinthedark.squatality.game.screens

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.squatality.game.Assets
import com.catinthedark.squatality.game.Const
import com.catinthedark.squatality.game.NetworkControl
import com.catinthedark.squatality.game.World
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.systems.*
import com.catinthedark.squatality.game.systems.network.PlayersSystem
import com.catinthedark.squatality.game.systems.network.RemoteSyncSystem
import com.catinthedark.squatality.models.HelloMessage

class GameScreen(
    private val stage: Stage,
    private val hudStage: Stage,
    private val nc: NetworkControl
) : YieldUnit<AssetManager, Any> {
    private val engine = PooledEngine()
    private lateinit var world: World
    private val TAG = "GameScreen"

    override fun onActivate(data: AssetManager) {
        Gdx.app.log(TAG, "GameScreen started")
        world = World(engine, data)
        engine.addEntity(world.createSync())

        engine.addSystem(RenderingSystem(stage, hudStage))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())
        //engine.addSystem(LocalMovementSystem())
        val rss = RemoteSyncSystem(nc.onGameState)
        engine.addSystem(rss)
        val ls = LerpSystem()
        engine.addSystem(ls)
        engine.addSystem(RemoteMovementSystem(nc.sender))
        engine.addSystem(PlayersSystem())
        engine.addSystem(KnobMovementSystem())
        engine.addSystem(KnobAimSystem())
        engine.addSystem(FollowCameraSystem(stage.camera))
        engine.addSystem(PerformanceSystem(hudStage, rss.getSyncDelta, ls.getLerpDelay))

        engine.addEntity(world.createField())

        nc.onGameStarted.subscribe { gsm ->
            val enemies = gsm.gameStateModel.players.filter { it.id != gsm.clientId }
            val me = gsm.gameStateModel.players.first { it.id == gsm.clientId }
            val mainPlayerComponent = world.createPlayer(me.id, me.x, me.y, Assets.PlayerSkin(data.get(Assets.Names.Player.BLUE)))
            engine.addEntity(mainPlayerComponent)
            engine.addEntity(world.createMovementKnob(30f, 20f, mainPlayerComponent.getComponent(MoveComponent::class.java), hudStage))
            engine.addEntity(world.createAimKnob(1000f, 20f, mainPlayerComponent.getComponent(AimComponent::class.java), hudStage))
            engine.addEntity(world.createCamera(mainPlayerComponent.getComponent(TransformComponent::class.java)))
            enemies.forEach { em ->
                val enemy = world.createUnit(em.id, em.x, em.y, Assets.PlayerSkin(data.get(Assets.Names.Player.RED)))
                engine.addEntity(enemy)
            }
        }

        nc.onEnemyConnected.subscribe {
            val enemy = world.createUnit(it.clientId, 0f, 0f, Assets.PlayerSkin(data.get(Assets.Names.Player.RED)))
            engine.addEntity(enemy)
        }

        Gdx.input.inputProcessor = hudStage

        nc.sender(HelloMessage(Const.Names.random()))
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
