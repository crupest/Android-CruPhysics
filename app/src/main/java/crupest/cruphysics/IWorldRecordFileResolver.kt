package crupest.cruphysics

import java.io.File

interface IWorldRecordFileResolver {
    fun getWorldFile(fileName: String): File
    fun getThumbnailFile(fileName: String): File
}
