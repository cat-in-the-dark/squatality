package com.catinthedark.squatality.server.spy

import com.catinthedark.squatality.server.spy.entities.PlayerEntity
import com.catinthedark.squatality.server.spy.entities.RoomEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Service to store all users' data in the DB.
 * Take a look. It should be async and run in different threads to not disturb the main game system.
 * It's not a pity if we lose some data, but if the game server experience lags - it's a big trouble.
 */
class SpyRepository() {
    private val log: Logger = LoggerFactory.getLogger(SpyRepository::class.java)

    fun save(room: RoomEntity) {
        log.info("Will save $room")
    }

    fun updatePlayer(player: PlayerEntity, roomId: UUID) {
        log.info("Will update player $player in room $roomId")
    }

    fun updateRoom(room: RoomEntity) {
        log.info("Will update room $room")
    }
}

