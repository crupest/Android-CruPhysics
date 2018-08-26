package crupest.cruphysics.data.world

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import crupest.cruphysics.utility.nowLong

@Entity(tableName = "world_record")
class WorldRecordEntity(
        @PrimaryKey @ColumnInfo(name = "timestamp") var timestamp: Long = nowLong(),
        @ColumnInfo(name = "world") var world: String = "",
        @ColumnInfo(name = "camera") var camera: String = "",
        @ColumnInfo(name = "thumbnail") var thumbnail: ByteArray = byteArrayOf()
)
