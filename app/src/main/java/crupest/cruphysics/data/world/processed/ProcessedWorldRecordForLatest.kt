package crupest.cruphysics.data.world.processed

import crupest.cruphysics.serialization.data.CameraData
import crupest.cruphysics.serialization.data.WorldData
import java.util.Date

data class ProcessedWorldRecordForLatest(val timestamp: Date, val world: WorldData, val camera: CameraData)
