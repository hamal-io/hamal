package io.hamal.lib.web3.evm.rlp

import java.math.BigInteger
import java.util.*

sealed interface RlpValue {

    data class String(val value: ByteArray) : RlpValue {
        constructor(value: kotlin.String) : this(value.toByteArray())
        constructor(value: Int) : this(value.toLong())
        constructor(value: Long) : this(BigInteger.valueOf(value))
        constructor(value: BigInteger) : this(value.bytes())

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as String
            return value.contentEquals(other.value)
        }

        override fun hashCode(): Int {
            return value.contentHashCode()
        }
    }

    data class List(val values: kotlin.collections.List<RlpValue>) : RlpValue {
        constructor(vararg values: RlpValue) : this(values.toList())

        companion object {
            val empty = List(listOf())
        }
    }

}

private fun BigInteger.bytes(): ByteArray {
    val bytes: ByteArray = toByteArray()
    return if (bytes[0].toInt() == 0) {
        Arrays.copyOfRange(bytes, 1, bytes.size)
    } else {
        bytes
    }
}