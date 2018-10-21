package crupest.cruphysics.data.world.entity

import androidx.room.ColumnInfo

class WorldRecordForHistory {
    @JvmField @ColumnInfo(name = "timestamp") var timestamp: Long = 0
    @JvmField @ColumnInfo(name = "world") var world: String = ""
    @JvmField @ColumnInfo(name = "camera") var camera: String = ""
    @JvmField @ColumnInfo(name = "thumbnail") var thumbnail: ByteArray = byteArrayOf()
}
