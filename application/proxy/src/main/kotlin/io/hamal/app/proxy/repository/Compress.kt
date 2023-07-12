package io.hamal.app.proxy.repository

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

internal fun ByteArray.zlibCompress(): ByteArray {
    val input = this
    val output = ByteArray(input.size)
    val compressor = Deflater().apply {
        setInput(input)
        finish()
    }
    return output.copyOfRange(0, compressor.deflate(output))
}

internal fun ByteArray.zlibDecompress(): ByteArray {
    val inflater = Inflater()
    val outputStream = ByteArrayOutputStream()

    return outputStream.use {
        val buffer = ByteArray(1024)

        inflater.setInput(this)

        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.toByteArray()
    }
}