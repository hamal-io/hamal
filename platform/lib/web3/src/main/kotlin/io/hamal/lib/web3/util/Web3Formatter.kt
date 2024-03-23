package io.hamal.lib.web3.util

import java.util.*
import kotlin.math.abs

object Web3Formatter {

    fun formatToHex(bytes: ByteArray): String = HexFormat.of().formatHex(bytes)

    fun formatWithoutLeadingZeros(bytes: ByteArray): String {
        val result = formatToHex(bytes)
        return result.replaceFirst("^0+(?!$)".toRegex(), "")
    }

    fun formatFixLength(bytes: ByteArray, length: Int): String {
        require(length > 0) { "Length must be >= 1" }

        var result = HexFormat.of().formatHex(bytes)
        val padding = length - result.length
        if (padding < 0) {
            return result.substring(abs(padding))
        }
        if (padding > 0) {
            result = "0".repeat(padding) + result
        }
        return result
    }
}