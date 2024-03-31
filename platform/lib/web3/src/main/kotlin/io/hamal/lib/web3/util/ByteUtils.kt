package io.hamal.lib.web3.util

object ByteUtils {
    fun trimLeadingZeroes(bytes: ByteArray) = bytes.dropWhile { it == 0.toByte() }.toByteArray()
}