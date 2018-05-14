package crupest.cruphysics.serialization

import com.squareup.moshi.Json
import java.util.*

data class WorldRecordData(
        var time: Date = Date(),
        @field:Json(name = "world_file") var worldFile: String = "",
        @field:Json(name = "thumbnail_file") var thumbnailFile: String =""
)
