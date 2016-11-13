package com.catinthedark.squatality.server.spy

import com.catinthedark.squatality.server.ServerGameEventsRegistrar
import com.catinthedark.squatality.server.spy.entities.PlayerEntity
import com.catinthedark.squatality.server.spy.entities.RoomEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class SpyService(
    private val ger: ServerGameEventsRegistrar,
    private val geoIPService: GeoIPService = GeoIPService(),
    private val repository: SpyRepository = SpyRepository()
) {
    private val log: Logger = LoggerFactory.getLogger(SpyService::class.java)
    private lateinit var onRoundEndsId: UUID

    fun register() {
        repository.register()
        onRoundEndsId = ger.onRoundEnds.subscribe { e ->
            log.info("EVENT $e")
            val roomEntity = RoomEntity(
                id = e.roomId.toString(),
                type = e.type,
                startedAt = e.startedAt,
                finishedAt = e.finishedAt,
                players = e.players.map { p ->
                    Pair(p.model.id.toString(), PlayerEntity(
                        id = p.model.id.toString(),
                        connectedAt = p.connectedAt,
                        deaths = p.model.deaths,
                        frags = p.model.frags,
                        disconnectedAt = p.disconnectedAt,
                        geo = null,
                        ip = p.address,
                        name = p.model.name
                    ))
                }.toMap()
            )
            repository.save(roomEntity)
            roomEntity.players.values.forEach { p ->
                if (p.ip == null) return@forEach
                geoIPService.find(p.ip).handleAsync { geoEntity, throwable ->
                    if (geoEntity != null) {
                        repository.updatePlayer(p.copy(geo = geoEntity), roomEntity.id)
                    } else {
                        log.error("Can't update player's geo: ${throwable.message}")
                    }
                }
            }
        }
    }

    fun dispose() {
        ger.onRoundEnds.unsubscribe(onRoundEndsId)
    }
}
