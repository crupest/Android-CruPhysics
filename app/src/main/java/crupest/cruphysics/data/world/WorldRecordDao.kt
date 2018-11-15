package crupest.cruphysics.data.world

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import crupest.cruphysics.data.world.entity.WorldRecordEntity
import crupest.cruphysics.data.world.entity.WorldRecordWithoutId

@Dao
interface WorldRecordDao {
    @Query("SELECT timestamp, world, camera, thumbnail FROM world_record ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): WorldRecordWithoutId?

    @Query("SELECT timestamp, world, camera, thumbnail FROM world_record ORDER BY timestamp DESC")
    fun getAllRecords(): List<WorldRecordWithoutId>

    @Insert
    fun insert(vararg record: WorldRecordEntity)

    @Query("UPDATE world_record SET timestamp = :newTimestamp WHERE timestamp = :oldTimestamp")
    fun updateTimestamp(oldTimestamp: Long, newTimestamp: Long)

    @Query("UPDATE world_record SET timestamp = :newTimestamp, camera = :camera, thumbnail = :thumbnail WHERE timestamp = :oldTimestamp")
    fun updateCamera(oldTimestamp: Long, newTimestamp: Long, camera: String, thumbnail: ByteArray)
}
