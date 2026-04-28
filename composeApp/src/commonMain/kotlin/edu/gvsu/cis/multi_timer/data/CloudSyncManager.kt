package edu.gvsu.cis.multi_timer.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.firstOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CloudSyncManager(private val dao: AppDAO) {
    private val firestore = Firebase.firestore

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getOrGenerateSyncKey(): String {
        var settings = dao.getSettings()
        if (settings == null) {
            // Generate a random 8-character alphanumeric string
            val newKey = Uuid.random().toString().substring(0, 8).uppercase()
            settings = AppSettings(syncKey = newKey)
            dao.saveSettings(settings)
        }
        return settings.syncKey
    }

    suspend fun pushToCloud() {
        val syncKey = getOrGenerateSyncKey()
        val players = dao.selectAllPlayers().firstOrNull() ?: emptyList()
        val playsets = dao.selectAllPlaysets().firstOrNull() ?: emptyList()

        try {
            // Push data to the specific syncKey document in Firestore
            firestore.collection("players").document(syncKey).set(CloudPlayers(players))
            firestore.collection("playsets").document(syncKey).set(CloudPlaysets(playsets))
        } catch (e: Exception) {
            println("Firebase Push Failed: ${e.message}")
        }
    }

    suspend fun pullFromCloud(syncKey: String) {
        try {
            // Download players
            val playersDoc = firestore.collection("players").document(syncKey).get()
            if (playersDoc.exists) {
                val cloudPlayers = playersDoc.data<CloudPlayers>()
                dao.removeAllPlayers()
                cloudPlayers.players.forEach { dao.insertPlayer(it) }
            }

            // Download playsets
            val playsetsDoc = firestore.collection("playsets").document(syncKey).get()
            if (playsetsDoc.exists) {
                val cloudPlaysets = playsetsDoc.data<CloudPlaysets>()
                dao.removeAllPlaysets()
                cloudPlaysets.playsets.forEach { dao.insertPlayset(it) }
            }

            // Update the local database sync key to match the new downloaded account
            dao.saveSettings(AppSettings(syncKey = syncKey))
        } catch (e: Exception) {
            println("Firebase Pull Failed: ${e.message}")
        }
    }
}