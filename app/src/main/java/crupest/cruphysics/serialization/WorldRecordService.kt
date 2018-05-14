package crupest.cruphysics.serialization

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import crupest.cruphysics.utility.showAlertDialog
import java.io.File

class WorldRecordService(val context: Context) {
    companion object {
        private const val RECORD_FILE_NAME = "record.json"
        private const val RECORD_WORLD_DIR_NAME = "worlds"
        private const val RECORD_THUMBNAIL_DIR_NAME = "thumbnails"
        private const val RECORD_FILE_FORMAT_ERROR_MESSAGE = "Record file is of wrong format."
    }

    private val recordFile: File = context.getFileStreamPath(RECORD_FILE_NAME)

    private val records: MutableList<WorldRecordData> =
            if (recordFile.exists())
                readRecordsFromString(recordFile.readText(), RECORD_FILE_FORMAT_ERROR_MESSAGE)
            else
                mutableListOf()

    private fun readRecordsFromString(string: String, errorMessage: String): MutableList<WorldRecordData> =
            try {
                string.fromJson()
            } catch (e: Exception) {
                if (context is Activity)
                    context.runOnUiThread {
                        showAlertDialog(context, errorMessage)
                    }
                mutableListOf()
            }

    private fun writeRecordsToFile() {
        recordFile.writeText(records.toJson())
    }

    fun getRecord(position: Int): WorldRecord = records[records.lastIndex - position].let {
        WorldRecord(it.time,
                context.getDir(RECORD_WORLD_DIR_NAME, MODE_PRIVATE).resolve(it.worldFile),
                context.getDir(RECORD_THUMBNAIL_DIR_NAME, MODE_PRIVATE).resolve(it.thumbnailFile)
        )
    }

    //TODO:record function.
}
