package com.catinthedark.squatality.server.spy

import com.catinthedark.squatality.server.spy.entities.PlayerEntity
import com.catinthedark.squatality.server.spy.entities.RoomEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Service to store all users' data in the DB.
 * Take a look. It should be async and run in different threads to not disturb the main game system.
 * It's not a pity if we lose some data, but if the game server experience lags - it's a big trouble.
 */
class SpyRepository() {
    private val log: Logger = LoggerFactory.getLogger(SpyRepository::class.java)

    fun register() {
        val options = FirebaseOptions.Builder()
            .setServiceAccount(ClassLoader.getSystemResourceAsStream("firebase_key.json"))
            .setDatabaseUrl("https://squatality-11624608.firebaseio.com")
            .build()

        FirebaseApp.initializeApp(options)
    }

    fun save(room: RoomEntity) {
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("server")
        val roomRef = ref.child("rooms")
        roomRef.child(room.id).setValue(room)
    }

    fun updatePlayer(player: PlayerEntity, roomId: String) {
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("server")
        val userRef = ref.child("rooms/$roomId/players/")
        userRef.child(player.id).setValue(player)
    }

    fun updateRoom(room: RoomEntity) {
        log.info("Will update room $room")
    }
}

