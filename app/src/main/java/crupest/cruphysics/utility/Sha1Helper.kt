package crupest.cruphysics.utility

import java.io.InputStream
import java.security.MessageDigest

fun InputStream.sha1Hex(): String = MessageDigest.getInstance("SHA-1").let {
    it.reset()
    it.update(this.readBytes())
    it.digest().byteArrayToHexString()
}


fun String.sha1Hex(): String = MessageDigest.getInstance("SHA-1").let {
    it.reset()
    it.update(this.toByteArray())
    it.digest().byteArrayToHexString()
}

fun ByteArray.byteArrayToHexString(): String {
    val hexChars = "0123456789ABCDEF".toCharArray()
    val chars = CharArray(2 * this.size)
    for ((index, value) in this.withIndex()) {
        chars[2 * index] = hexChars[(value.toInt() and 0xF0) ushr 4]
        chars[2 * index + 1] = hexChars[value.toInt() and 0x0F]
    }
    return String(chars)
}
