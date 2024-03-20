package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.util.Web3Formatter
import java.math.BigInteger

sealed class EvmUnsigned : EvmType<BigInteger> {
    abstract val numberOfBits: Int

    override fun toByteArray(): ByteArray = value.toByteArray()

    override fun toByteWindow() = toPrefixedHexString().toByteWindow()

    fun toPrefixedHexString(): EvmPrefixedHexString = EvmPrefixedHexString(
        "0x${Web3Formatter.formatWithoutLeadingZeros(value.toByteArray())}"
    )

    protected fun ensureValidValue() {
        require(value >= BigInteger.ZERO) { "Value must be positive" }
        val maxValue = BigInteger.ONE.shiftLeft(numberOfBits).subtract(BigInteger.ONE)
        require(value <= maxValue) { "Value must be <= $maxValue" }
    }
}

data class EvmUint8(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 8

    constructor(value: Byte) : this(BigInteger.valueOf(value.toLong()))
    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint16(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 16

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint32(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 32

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: Int) : this(BigInteger.valueOf(value.toLong()))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint64(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 64

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint112(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 112

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint128(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 128

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint160(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 160

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}

data class EvmUint256(override val value: BigInteger) : EvmUnsigned() {
    override val numberOfBits = 256

    constructor(value: Long) : this(BigInteger.valueOf(value))
    constructor(value: String) : this(EvmPrefixedHexString(value))
    constructor(value: EvmPrefixedHexString) : this(BigInteger(value.toHexString().value, 16))

    init {
        ensureValidValue()
    }

}