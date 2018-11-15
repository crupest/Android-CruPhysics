package crupest.cruphysics.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Bitmap.compressToPng(): ByteArray {
    return ByteArrayOutputStream().also {
        this.compress(Bitmap.CompressFormat.PNG, 100, it)
    }.toByteArray()
}

fun ByteArray.decompressToBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
            ?: throw RuntimeException("Failed to decompress thumbnail.")
}
