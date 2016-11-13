package com.catinthedark.squatality.server

import com.catinthedark.lib.IObservable
import com.catinthedark.lib.ScheduledObservable
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

interface ServerGameEvent // marker interface

data class ServerRoundEndedEvent(
    val players: List<RoomService.Player>,
    val roomId: UUID,
    val startedAt: Long,
    val finishedAt: Long,
    val type: String
) : ServerGameEvent

class ServerGameEventsRegistrar(
    private val executor: Executor = Executors.newFixedThreadPool(4)
) {
    val onRoundEnds: IObservable<ServerRoundEndedEvent> = ScheduledObservable(executor)

    fun dispose() {
        onRoundEnds.clear()
    }
}
