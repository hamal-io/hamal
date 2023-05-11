package io.hamal.lib.common.util

object BitUtils {
    fun extractRange(value: Long, startIndex: Int, numberOfBits: Int): Long{
        return (1L shl numberOfBits) - 1L and (value shr startIndex)
    }
}