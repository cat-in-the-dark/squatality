package com.catinthedark.squatality.game.screens

import com.badlogic.ashley.core.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.lib.ashley.getComponent
import com.catinthedark.squatality.game.*
import com.catinthedark.squatality.game.components.*
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
    private var engine: Engine? = null
    private var world: World? = null
    private lateinit var am: AssetManager
    private val TAG = "GameScreen"
    private var disconnected: Boolean = false

    override fun onActivate(data: AssetManager) {
        Gdx.app.log(TAG, "GameScreen started")
        disconnected = false
        am = data
        val e = PooledEngine()
        engine = e
        val w = World(e, am)
        world = w
        e.addEntity(w.createSync())
        val notificationsEntity = w.createNotifications()
        e.addEntity(notificationsEntity)

        e.addSystem(RenderingSystem(stage, hudStage))
        e.addSystem(AnimationSystem())
        e.addSystem(StateSystem())
        //e.addSystem(LocalMovementSystem())
        val rss = RemoteSyncSystem(nc.onGameState)
        e.addSystem(rss)
        val ls = LerpSystem()
        e.addSystem(ls)
        e.addSystem(RemoteMovementSystem(nc.senderUnreliable))
        e.addSystem(PlayersSystem())
        e.addSystem(BricksSystem(w))
        e.addSystem(BonusesSystem(w))
        e.addSystem(KnobMovementSystem(hudStage))
        e.addSystem(KnobAimSystem(hudStage, nc.sender))
        e.addSystem(FollowCameraSystem(stage.camera))
        e.addSystem(UIPerformanceSystem(hudStage, rss.getSyncDelta, ls.getLerpDelay, nc.latency))
        e.addSystem(UIClockSystem(hudStage))
        e.addSystem(FollowingTransformSystem())
        e.addSystem(UINotificationsSystem(hudStage))
        e.addSystem(UIPlayersListSystem(hudStage, am))

        e.addEntity(w.createField())
        w.createFans().forEach { e.addEntity(it) }

        nc.onGameStarted.subscribe { gsm ->
            val enemies = gsm.gameStateModel.players.filter { it.id != gsm.clientId }
            val me = gsm.gameStateModel.players.first { it.id == gsm.clientId }
            val mainPlayerComponent = w.createPlayer(me.id, me.x, me.y, Assets.PlayerSkin(am.get(Assets.Names.Player.BLUE)))
            e.addEntity(mainPlayerComponent)
            e.addEntity(w.createMovementKnob(30f, 20f, mainPlayerComponent.getComponent(), hudStage))
            e.addEntity(w.createAimKnob(1000f, 20f, mainPlayerComponent.getComponent(), mainPlayerComponent.getComponent(), mainPlayerComponent.getComponent(), hudStage))
            e.addEntity(w.createCamera(mainPlayerComponent.getComponent()))
            enemies.forEach { em ->
                val enemy = w.createUnit(em.id, em.x, em.y, Assets.PlayerSkin(am.get(Assets.Names.Player.RED)))
                e.addEntity(enemy)
            }
        }

        e.addEntityListener(Family.all(RemoteIDComponent::class.java, TransformComponent::class.java, StateComponent::class.java).get(), object : EntityListener {
            override fun entityRemoved(entity: Entity?) {
                //TODO: should we remove hat entity?
            }

            override fun entityAdded(entity: Entity?) {
                val tc: TransformComponent = entity?.getComponent() ?: return
                val sc: StateComponent = entity?.getComponent() ?: return
                e.addEntity(w.createHat(tc, sc))
            }
        })

        nc.onEnemyConnected.subscribe {
            val enemy = w.createUnit(it.clientId, 0f, 0f, Assets.PlayerSkin(am.get(Assets.Names.Player.RED)))
            e.addEntity(enemy)
        }

        nc.onEnemyDisconnected.subscribe { msg ->
            val enemy = e.getEntitiesFor(Family.all(RemoteIDComponent::class.java).get()).find { e ->
                Mappers.remote.id[e].id == msg.clientId
            }
            e.removeEntity(enemy)
        }

        nc.onDisconnected.subscribe { msg ->
            disconnected = true
        }

        nc.onKilled.subscribe { msg ->
            val component: UINotificationComponent = notificationsEntity.getComponent()
            component.list.add(Notification(
                text = "${msg.killerNames.joinToString(", ")} -> ${msg.victimName}")
            )
        }

        Gdx.input.inputProcessor = hudStage

        nc.sender(HelloMessage(Const.Names.random()))
    }

    override fun run(delta: Float): AssetManager? {
        if (disconnected) return am
        world?.engine?.update(delta)
        return null
    }

    override fun onExit() {
        Gdx.app.log(TAG, "onExit")
        nc.dispose()
        stage.dispose()
        hudStage.dispose()
        world = null
        engine = null
        Gdx.app.log(TAG, "exited")
    }
}
