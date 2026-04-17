package edu.gvsu.cis.multi_timer.data

import kotlinx.coroutines.flow.Flow

class FakeAppDAO(): AppDAO {
    override suspend fun insert(gs: Playset) {
        TODO("Not yet implemented")
    }

    override suspend fun modifyTable(gs: Playset) {
        TODO("Not yet implemented")
    }

    override suspend fun someAction(gs: Playset) {
        TODO("Not yet implemented")
    }

    override suspend fun removeOne(gs: Playset) {
        TODO("Not yet implemented")
    }

    override suspend fun removeAll() {
        TODO("Not yet implemented")
    }

    override suspend fun selectAllAsList(): List<Playset> {
        TODO("Not yet implemented")
    }

    override fun selectAll(): Flow<List<Playset>> {
        TODO("Not yet implemented")
    }

}