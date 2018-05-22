package crupest.cruphysics

import java.io.File

interface IWorldRecordFileResolver {
    fun getThumbnailFile(fileName: String): File
}
