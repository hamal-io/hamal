package io.hamal.lib.util

object Hex {
    fun isValidHexNumber(value: String): Boolean{
        if(value.trim().isEmpty()){
            return false
        }
        return if (value.startsWith("0x")) {
            validHexNumberWithoutPrefix(value.substring(2, value.length))
        } else {
            validHexNumberWithoutPrefix(value)
        }
    }
}

fun validHexNumberWithoutPrefix(value: String) = value.asSequence().all { it in "0123456789ABCDEFabcdef" }
