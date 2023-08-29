package io.hamal.lib.common.util

import java.nio.charset.Charset
import java.security.MessageDigest


object HashUtils {
    fun sha256(value: String): String = sha256(value.toByteArray(Charset.defaultCharset()))
    fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return apply(bytes, digest)
    }

    fun md5(value: String): String = md5(value.toByteArray(Charset.defaultCharset()))

    fun md5(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("MD5")
        return apply(bytes, digest)
    }

    private fun apply(bytes: ByteArray, digest: MessageDigest): String {
        val resultBuilder = StringBuilder()
        digest.digest(bytes).forEach {
            val hex = Integer.toHexString(0xff and it.toInt())
            if (hex.length == 1) {
                resultBuilder.append('0')
            }
            resultBuilder.append(hex)
        }

        return resultBuilder.toString()
    }
}

