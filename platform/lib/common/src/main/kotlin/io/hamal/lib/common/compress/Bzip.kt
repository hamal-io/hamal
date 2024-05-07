package io.hamal.lib.common.compress

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

object CompressorBzip : Compressor {

    override fun compress(uncompressed: ByteArray): ByteArray {
        val byteStream = ByteArrayOutputStream(uncompressed.size)
        return try {
            BZip2CompressorOutputStream(byteStream)
                .also { it.write(uncompressed) }
                .also { it.close() }

            byteStream.toByteArray().also { byteStream.close() }
        } finally {
            try {
                byteStream.close()
            } catch (_: IOException) {
            }
        }
    }

    override fun toArray(compressed: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream()
        val bis = ByteArrayInputStream(compressed)
        val bzip2 = BZip2CompressorInputStream(bis)

        val buff = ByteArray(BUFFER_SIZE)
        var n: Int
        while ((bzip2.read(buff, 0, BUFFER_SIZE).also { n = it }) > 0) {
            bos.write(buff, 0, n)
        }
        return bos.toByteArray()
    }

    private const val BUFFER_SIZE = 8192
}