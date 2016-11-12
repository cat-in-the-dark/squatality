package com.catinthedark.squatality.server.spy

import com.catinthedark.squatality.server.spy.entities.PlayerEntity
import com.catinthedark.squatality.server.spy.entities.RoomEntity
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Service to store all users' data in the DB.
 * Take a look. It should be async and run in different threads to not disturb the main game system.
 * It's not a pity if we lose some data, but if the game server experience lags - it's a big trouble.
 */
class Repository(
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(4),
    private val geoIPService: GeoIPService = GeoIPService()
) {
    fun save(room: RoomEntity) {
        executor.submit {  }
    }

    fun updatePlayer(player: PlayerEntity, roomId: UUID) {
        executor.submit {  }
    }

    fun updateRoom(room: RoomEntity) {
        executor.submit {  }
    }
}

