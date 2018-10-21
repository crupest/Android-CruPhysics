package crupest.cruphysics.data.world.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_record")
class WorldRecordEntity {
    @JvmField @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0
    @JvmField @ColumnInfo(name = "timestamp") var timestamp: Long = 0
    @JvmField @ColumnInfo(name = "world") var world: String = ""
    @JvmField @ColumnInfo(name = "camera") var camera: String = ""
    @JvmField @ColumnInfo(name = "thumbnail") var thumbnail: ByteArray = byteArrayOf()
}
