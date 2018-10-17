package crupest.cruphysics.data.world

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorldRecordDao {
    @Query("SELECT * FROM world_record ORDER BY timestamp DESC")
    fun getRecords(): LiveData<List<WorldRecordEntity>>

    @Query("SELECT * FROM world_record ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): WorldRecordEntity?

    @Insert
    fun insert(vararg record: WorldRecordEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg record: WorldRecordEntity)
}
