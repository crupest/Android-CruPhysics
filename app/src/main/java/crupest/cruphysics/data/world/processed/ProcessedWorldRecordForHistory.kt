package crupest.cruphysics.data.world.processed

import android.graphics.Bitmap
import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import java.util.Date

data class ProcessedWorldRecordForHistory(val id: Long, val timestamp: Date, val world: WorldData, val camera: CameraData, val thumbnail: Bitmap)
