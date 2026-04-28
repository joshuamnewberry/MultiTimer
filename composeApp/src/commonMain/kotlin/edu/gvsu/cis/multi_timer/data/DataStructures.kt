package edu.gvsu.cis.multi_timer.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class CounterMode { TIMER, STOPWATCH, LIFE }

@Serializable
data class AutoAdvanceConfiguration(
    val enabled: Boolean = true,
    val reversed: Boolean = false,
    val inversed: Boolean = false
)

@Entity
@Serializable
data class Player(
    var name: String = "Player",
    var profilePicture: String? = null,
    var playerBackgroundColor: Long = 0xFFFFFFFF,
    @PrimaryKey(autoGenerate = true) val playerID: Int = 1
)

@Serializable
data class PlayerActiveState(
    val playerProfileId: Int,
    val mode: CounterMode,
    val currentValue: Long,
    val isRunning: Boolean,
    val isCurrentTurn: Boolean = false
)

@Entity
@Serializable
data class Playset(
    val name: String = "Playset",
    val playerCount: Int = 2,
    var fullBackgroundColorArgb: Long = 0xFFFFFFFF,
    var fullBackgroundImage: String? = null,
    val counterTypesJson: String = "",
    @Embedded val autoAdvance: AutoAdvanceConfiguration = AutoAdvanceConfiguration(),
    val incrementSeconds: Int = 0,
    val startingLife: Int = 40,
    val startingTimerSeconds: Int = 300,
    @PrimaryKey(autoGenerate = true) val playsetID: Int = 0
)

@Entity
data class ActiveGameState(
    @Embedded val currentPlayset: Playset,
    val currentPlayersState: List<PlayerActiveState>,
    val isGamePaused: Boolean = true,
    val hasGameStarted: Boolean = false,
    @PrimaryKey val activeGameStateID: Int = 0
)

@Entity
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val syncKey: String
)

@Serializable
data class CloudPlaysets(val playsets: List<Playset>)

@Serializable
data class CloudPlayers(val players: List<Player>)