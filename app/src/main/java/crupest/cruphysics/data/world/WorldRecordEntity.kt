package crupest.cruphysics.data.world

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import crupest.cruphysics.utility.nowLong

@Entity(tableName = "world_record")
class WorldRecordEntity {
    @JvmField @PrimaryKey @ColumnInfo(name = "timestamp") var timestamp: Long = nowLong()
    @JvmField @ColumnInfo(name = "world") var world: String = ""
    @JvmField @ColumnInfo(name = "camera") var camera: String = ""
    @JvmField @ColumnInfo(name = "thumbnail") var thumbnail: ByteArray = byteArrayOf()
}
