package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.Web3Formatter
import java.math.BigInteger

sealed class EthUnsigned : EthType<BigInteger> {
    abstract val numberOfBits: Int

    override fun toByteArray(): ByteArray = value.toByteArray()

    override fun toByteWindow() = toPrefixedHexString().toByteWindow()

    fun toPrefixedHexString(): EthPrefixedHexString = EthPrefixedHexString(
        "0x${Web3Formatter.formatWithoutLeadingZeros(value.toByteArray())}"
    )

    protected fun ensureValidValue() {
        require(value >= BigInteger.ZERO) { "Value must be positive" }
        val maxValue = BigInteger.ONE.shiftLeft(numberOfBits).subtract(BigInteger.ONE)
        require(value <= maxValue) { "Value must be <= $maxValue" }
    }
}

data class EthUint8(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 8

    constructor(value: Byte) : this(BigInteger.valueOf(value.toLong()))
    constructor(value: EthPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EthUint16(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 16

    init {
        ensureValidValue()
    }

}

data class EthUint32(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 32

    init {
        ensureValidValue()
    }

}

data class EthUint64(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 64

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: EthPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EthUint112(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 112

    init {
        ensureValidValue()
    }

}

data class EthUint128(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 128

    init {
        ensureValidValue()
    }

}

data class EthUint160(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 160

    init {
        ensureValidValue()
    }

}

data class EthUint256(override val value: BigInteger) : EthUnsigned() {
    constructor(value: EthPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))
    override val numberOfBits = 256

    init {
        ensureValidValue()
    }

}