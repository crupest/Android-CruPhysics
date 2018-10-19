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

    @Query("UPDATE world_record SET timestamp = :timestamp WHERE id = :id")
    fun updateTimestamp(id: Long, timestamp: Long)

    @Query("UPDATE world_record SET camera = :camera, thumbnail = :thumbnail WHERE timestamp = :timestamp")
    fun updateCameraByTimestamp(timestamp: Long, camera: String, thumbnail: ByteArray)
}
