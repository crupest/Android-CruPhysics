package crupest.cruphysics.data.world

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface WorldRecordDao {
    @Query("SELECT * FROM world_record ORDER BY timestamp DESC")
    fun getRecords(): List<WorldRecord>

    @Insert
    fun insert(record: WorldRecord)
}
