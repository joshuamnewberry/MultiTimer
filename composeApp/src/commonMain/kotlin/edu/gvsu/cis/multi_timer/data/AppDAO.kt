@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package edu.gvsu.cis.multi_timer.data

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDAO {
    @Insert
    suspend fun insert(gs: Playset)

    @Update
    suspend fun modifyTable(gs: Playset)

    @Upsert
    suspend fun someAction(gs: Playset)

    @Delete
    suspend fun removeOne(gs: Playset)

    @Query("DELETE FROM Playset")
    suspend fun removeAll()

    @Query("SELECT * FROM Playset")
    suspend fun selectAllAsList(): List<Playset>

    @Query("SELECT * FROM Playset")
    fun selectAll(): Flow<List<Playset>>
}

@Database(
    entities = [Playset::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(MyDatabaseBuilder::class)
abstract class AppDB: RoomDatabase() {
    abstract fun getDao(): AppDAO
}

expect object MyDatabaseBuilder: RoomDatabaseConstructor<AppDB> {
    override fun initialize(): AppDB
}

fun getDatabaseInstance(builder: RoomDatabase.Builder<AppDB>): AppDB {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}