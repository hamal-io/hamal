package io.hamal.lib.common.hot

import java.math.BigDecimal
import java.math.BigInteger

sealed interface HotTerminal : HotNode {

    override val isTerminal get() : Boolean = true

    fun asBigDecimal(): BigDecimal = throw NotImplementedError("asBigDecimal() not supported")
    fun asBigInt(): BigInteger = throw NotImplementedError("asBigInt() not supported")
//    override fun asBoolean(): Boolean = throw NotImplementedError("asBoolean() not supported")


    fun asString(): String {
        throw NotImplementedError("asString() not supported")
    }

    fun asByte(): Byte {
        throw NotImplementedError("asByte() not supported")
    }

    fun asShort(): Short {
        throw NotImplementedError("asShort() not supported")
    }

    fun asInt(): Int {
        throw NotImplementedError("asInt() not supported")
    }

    fun asLong(): Long {
        throw NotImplementedError("asLong() not supported")
    }

    fun asFloat(): Float {
        throw NotImplementedError("asFloat() not supported")
    }

    fun asDouble(): Double {
        throw NotImplementedError("asDouble() not supported")
    }

    fun asByteArray(): ByteArray {
        throw NotImplementedError("asByteArray() not supported")
    }

}