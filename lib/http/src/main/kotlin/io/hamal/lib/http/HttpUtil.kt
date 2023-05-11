package io.hamal.lib.http

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

object HttpUtil {
    private fun convert(inputStream: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        var read: Int
        val maxSize = 4096 * 4
        val buffer = ByteArray(maxSize)
        while (inputStream.read(buffer, 0, maxSize).also { read = it } != -1) {
            bos.write(buffer, 0, read)
        }
        return bos.toByteArray()
    }

    fun copyStream(inputStream: InputStream?): InputStream {
        return if (inputStream == null) {
            ByteArrayInputStream(ByteArray(0))
        } else{
            ByteArrayInputStream(convert(inputStream))
        }
    }
}