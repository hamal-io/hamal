package io.hamal.lib.core.util

object Bitwise {
    fun extractRange(value: Long, startIndex: Int, numberOfBits: Int): Long{
        return (1L shl numberOfBits) - 1L and (value shr startIndex)
    }
}