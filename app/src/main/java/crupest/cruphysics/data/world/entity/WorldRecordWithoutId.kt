package crupest.cruphysics.data.world.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import crupest.cruphysics.serialization.fromJson
import crupest.cruphysics.utility.decompressToBitmap
import java.util.*

class WorldRecordWithoutId {
    @JvmField @ColumnInfo(name = "timestamp") var timestamp: Long = 0
    @JvmField @ColumnInfo(name = "world") var world: String = ""
    @JvmField @ColumnInfo(name = "camera") var camera: String = ""
    @JvmField @ColumnInfo(name = "thumbnail") var thumbnail: ByteArray = byteArrayOf()
}

data class ProcessedWorldRecordWithoutId(val timestamp: Date, val world: WorldData, val camera: CameraData, val thumbnail: Bitmap)

fun WorldRecordWithoutId.process(): ProcessedWorldRecordWithoutId {
    return ProcessedWorldRecordWithoutId(Date(this.timestamp), this.world.fromJson(), this.camera.fromJson(), this.thumbnail.decompressToBitmap())
}
