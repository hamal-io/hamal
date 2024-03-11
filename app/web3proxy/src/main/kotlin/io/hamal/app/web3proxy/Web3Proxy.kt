package io.hamal.app.web3proxy

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.ByteArrayOutputStream
import java.io.IOException


@SpringBootApplication
class Proxy

fun main(args: Array<String>) {
    runApplication<Proxy>(*args)
}

fun compress(data: ByteArray?): ByteArray? {
    if (data == null) return null
    var compressedData: ByteArray? = null
    try {
        val byteStream = ByteArrayOutputStream(data.size)
        try {
            val bout = BZip2CompressorOutputStream(byteStream)
            try {
                bout.write(data)
            } finally {
                try {
                    bout.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } finally {
            try {
                byteStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        compressedData = byteStream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return compressedData
}