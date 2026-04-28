@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package edu.gvsu.cis.multi_timer.data

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {
    // Playset Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayset(gs: Playset)

    @Update
    suspend fun modifyPlayset(gs: Playset)

    @Delete
    suspend fun removePlayset(gs: Playset)

    @Query("DELETE FROM Playset")
    suspend fun removeAllPlaysets()

    @Query("SELECT * FROM Playset")
    fun selectAllPlaysets(): Flow<List<Playset>>

    @Query("SELECT * FROM Playset WHERE playsetID = :id")
    suspend fun getPlaysetById(id: Int): Playset?

    // Player Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun modifyPlayer(player: Player)

    @Delete
    suspend fun removePlayer(player: Player)

    @Query("DELETE FROM Playset")
    suspend fun removeAllPlayers()

    @Query("SELECT * FROM Player")
    fun selectAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM Player WHERE playerID = :id")
    suspend fun getPlayerById(id: Int): Player?

    // Active Game Queries
    @Query("SELECT * FROM ActiveGameState WHERE activeGameStateID = 0")
    fun getActiveGame(): Flow<ActiveGameState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertActiveGame(gameState: ActiveGameState)

    @Query("DELETE FROM ActiveGameState")
    suspend fun clearActiveGame()

    @Query("SELECT * FROM AppSettings WHERE id = 1")
    suspend fun getSettings(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}

@Database(
    entities = [Playset::class, Player::class, ActiveGameState::class, AppSettings::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
@ConstructedBy(MyDatabaseBuilder::class)
abstract class AppDB: RoomDatabase() {
    abstract fun getDao(): AppDAO
}

expect object MyDatabaseBuilder: RoomDatabaseConstructor<AppDB> {
    override fun initialize(): AppDB
}

fun getDatabaseInstance(builder: RoomDatabase.Builder<AppDB>): AppDB {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}