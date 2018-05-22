package crupest.cruphysics.data.world

import android.arch.persistence.room.*

@Dao
interface WorldRecordDao {
    @Query("SELECT * FROM world_record ORDER BY timestamp DESC")
    fun getRecords(): List<WorldRecord>

    @Insert
    fun insert(vararg record: WorldRecord)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg record: WorldRecord)
}
