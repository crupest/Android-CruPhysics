package crupest.cruphysics.data.world.entity

import androidx.room.ColumnInfo

class WorldRecordForLatest {
    @JvmField @ColumnInfo(name = "timestamp") var timestamp: Long = 0
    @JvmField @ColumnInfo(name = "world") var world: String = ""
    @JvmField @ColumnInfo(name = "camera") var camera: String = ""
}
