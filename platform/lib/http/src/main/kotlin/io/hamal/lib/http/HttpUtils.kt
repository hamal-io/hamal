package io.hamal.lib.http

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object HttpUtils {

    @Throws(IOException::class)
    fun convert(`is`: InputStream): ByteArray? {
        var len: Int
        val size = 4096 * 4
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(size)
        while (`is`.read(buffer, 0, size).also { len = it } != -1) {
            bos.write(buffer, 0, len)
        }
        return bos.toByteArray()
    }

    fun copyStream(inputStream: InputStream?): InputStream? {
        return if (inputStream == null) {
            ByteArrayInputStream(ByteArray(0))
        } else {
            ByteArrayInputStream(convert(inputStream))
        }
    }

}