package crupest.cruphysics.data.world

import androidx.room.*
import io.reactivex.Flowable

@Dao
interface WorldRecordDao {
    @Query("SELECT * FROM world_record ORDER BY timestamp DESC")
    fun getRecords(): Flowable<List<WorldRecordEntity>>

    @Query("SELECT * FROM world_record ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): WorldRecordEntity?

    @Insert
    fun insert(vararg record: WorldRecordEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg record: WorldRecordEntity)
}
