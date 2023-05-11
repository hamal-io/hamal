package io.hamal.lib.common.util

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest


object Hash {
    fun sha256(value: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val resultBuilder = StringBuilder()

            digest.digest(value.toByteArray(UTF_8)).forEach {
                val hex = Integer.toHexString(0xff and it.toInt())
                if (hex.length == 1) {
                    resultBuilder.append('0')
                }
                resultBuilder.append(hex)
            }

            resultBuilder.toString()
        } catch (e: Exception) {
            throw e
        }
    }
}

