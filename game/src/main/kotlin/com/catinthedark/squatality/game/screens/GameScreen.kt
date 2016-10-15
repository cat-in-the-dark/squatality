package com.catinthedark.squatality.game.screens

import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.ashley.getComponent
import com.catinthedark.squatality.game.*
import com.catinthedark.squatality.game.components.RemoteIDComponent
import com.catinthedark.squatality.game.systems.*
import com.catinthedark.squatality.game.systems.network.BonusesSystem
import com.catinthedark.squatality.game.systems.network.BricksSystem
import com.catinthedark.squatality.game.systems.network.PlayersSystem
import com.catinthedark.squatality.game.systems.network.RemoteSyncSystem
import com.catinthedark.squatality.models.HelloMessage

class GameScreen(
    private val stage: Stage,
    private val hudStage: Stage,
    private val nc: NetworkControl
) : YieldUnit<AssetManager, AssetManager> {
    private val engine = PooledEngine()
    private lateinit var world: World
    private lateinit var am: AssetManager
    private val TAG = "GameScreen"
    private var disconnected: Boolean = false

    override fun onActivate(data: AssetManager) {
        Gdx.app.log(TAG, "GameScreen started")
        disconnected = false
        am = data
        world = World(engine, am)
        engine.addEntity(world.createSync())

        engine.addSystem(RenderingSystem(stage, hudStage))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())
        //engine.addSystem(LocalMovementSystem())
        val rss = RemoteSyncSystem(nc.onGameState)
        engine.addSystem(rss)
        val ls = LerpSystem()
        engine.addSystem(ls)
        engine.addSystem(RemoteMovementSystem(nc.senderUnreliable))
        engine.addSystem(PlayersSystem())
        engine.addSystem(BricksSystem(world))
        engine.addSystem(BonusesSystem(world))
        engine.addSystem(KnobMovementSystem(hudStage))
        engine.addSystem(KnobAimSystem(hudStage, nc.sender))
        engine.addSystem(FollowCameraSystem(stage.camera))
        engine.addSystem(PerformanceSystem(hudStage, rss.getSyncDelta, ls.getLerpDelay, nc.latency))
        engine.addSystem(ClockSystem(hudStage))

        engine.addEntity(world.createField())
        world.createFans().forEach { engine.addEntity(it) }

        nc.onGameStarted.subscribe { gsm ->
            val enemies = gsm.gameStateModel.players.filter { it.id != gsm.clientId }
            val me = gsm.gameStateModel.players.first { it.id == gsm.clientId }
            val mainPlayerComponent = world.createPlayer(me.id, me.x, me.y, Assets.PlayerSkin(am.get(Assets.Names.Player.BLUE)))
            engine.addEntity(mainPlayerComponent)
            engine.addEntity(world.createMovementKnob(30f, 20f, mainPlayerComponent.getComponent(), hudStage))
            engine.addEntity(world.createAimKnob(1000f, 20f, mainPlayerComponent.getComponent(), mainPlayerComponent.getComponent(), mainPlayerComponent.getComponent(), hudStage))
            engine.addEntity(world.createCamera(mainPlayerComponent.getComponent()))
            enemies.forEach { em ->
                val enemy = world.createUnit(em.id, em.x, em.y, Assets.PlayerSkin(am.get(Assets.Names.Player.RED)))
                engine.addEntity(enemy)
            }
        }

        nc.onEnemyConnected.subscribe {
            val enemy = world.createUnit(it.clientId, 0f, 0f, Assets.PlayerSkin(am.get(Assets.Names.Player.RED)))
            engine.addEntity(enemy)
        }

        nc.onEnemyDisconnected.subscribe { msg ->
            val enemy = engine.getEntitiesFor(Family.all(RemoteIDComponent::class.java).get()).find { e ->
                Mappers.remote.id[e].id == msg.clientId
            }
            engine.removeEntity(enemy)
        }

        nc.onDisconnected.subscribe { msg ->
            disconnected = true
        }

        Gdx.input.inputProcessor = hudStage

        nc.sender(HelloMessage(Const.Names.random()))
    }

    override fun run(delta: Float): AssetManager? {
        if (disconnected) return am
        engine.update(delta)
        return null
    }

    override fun onExit() {
        engine.removeAllEntities()
        stage.dispose()
        hudStage.dispose()
        nc.dispose()
    }
}
