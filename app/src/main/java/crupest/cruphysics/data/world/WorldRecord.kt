package crupest.cruphysics.data.world

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "world_record")
class WorldRecord(
        @PrimaryKey(autoGenerate = true) var id: Int = 0,
        @ColumnInfo(name = "timestamp") var timestamp: Long = Date().time,
        @ColumnInfo(name = "world_file") var worldFile: String = "",
        @ColumnInfo(name = "thumbnail_file") var thumbnailFile: String = ""
)
