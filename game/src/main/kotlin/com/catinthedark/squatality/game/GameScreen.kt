package com.catinthedark.squatality.game

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.Stage
import com.catinthedark.lib.YieldUnit
import com.catinthedark.squatality.game.components.AimComponent
import com.catinthedark.squatality.game.components.MoveComponent
import com.catinthedark.squatality.game.components.TransformComponent
import com.catinthedark.squatality.game.systems.*

class GameScreen(
    private val stage: Stage,
    private val hudStage: Stage
) : YieldUnit<AssetManager, Any> {
    private val engine = PooledEngine()
    private val nc = NetworkControl(Const.Network.server)
    private lateinit var ncThread: Thread
    private lateinit var world: World

    override fun onActivate(data: AssetManager) {
        println("GameScreen started")
        world = World(engine, data)
        engine.addSystem(RenderingSystem(stage, hudStage))
        engine.addSystem(AnimationSystem())
        engine.addSystem(StateSystem())
        engine.addSystem(LocalMovementSystem())
        //engine.addSystem(RandomControlSystem())
        engine.addSystem(KnobMovementSystem())
        engine.addSystem(KnobAimSystem())
        engine.addSystem(CameraSystem(stage.camera))

        val mainPlayerComponent = world.createPlayer(0f, 0f, Assets.PlayerSkin(data.get(Assets.Names.Player.BLUE)))
        engine.addEntity(mainPlayerComponent)
        engine.addEntity(world.createPlayer(200f, 200f, Assets.PlayerSkin(data.get(Assets.Names.Player.RED))))
        engine.addEntity(world.createPlayer(400f, 200f, Assets.PlayerSkin(data.get(Assets.Names.Player.BLACK))))
        engine.addEntity(world.createPlayer(600f, 600f, Assets.PlayerSkin(data.get(Assets.Names.Player.RED))))
        engine.addEntity(world.createField())
        engine.addEntity(world.createMovementKnob(15f, 15f, mainPlayerComponent.getComponent(MoveComponent::class.java), hudStage))
        engine.addEntity(world.createAimKnob(1015f, 15f, mainPlayerComponent.getComponent(AimComponent::class.java), hudStage))
        engine.addEntity(world.createCamera(mainPlayerComponent.getComponent(TransformComponent::class.java)))

        Gdx.input.inputProcessor = hudStage
        ncThread = Thread(nc)
        ncThread.start()
    }

    override fun run(delta: Float): Any? {
        engine.update(delta)
        return null
    }

    override fun onExit() {
        engine.removeAllEntities()
        stage.dispose()
        nc.dispose()
        ncThread.interrupt()
    }
}
