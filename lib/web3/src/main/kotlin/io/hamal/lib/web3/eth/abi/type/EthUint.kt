package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.Web3Formatter
import java.math.BigInteger

sealed class EthUnsigned(
    final override val value: BigInteger,
    numberOfBits: Int
) : EthType<BigInteger> {
    init {
        require(value >= BigInteger.ZERO) { "Value must be positive" }
        val maxValue = BigInteger.ONE.shiftLeft(numberOfBits).subtract(BigInteger.ONE)
        require(value <= maxValue) { "Value must be <= $maxValue" }
    }

    override fun toByteArray(): ByteArray = value.toByteArray()

    override fun toByteWindow() = toEthPrefixedString().toByteWindow()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EthUnsigned
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    private fun toEthPrefixedString(): EthPrefixedHexString = EthPrefixedHexString(
        "0x${Web3Formatter.formatWithoutLeadingZeros(value.toByteArray())}"
    )
}

class EthUint8(value: BigInteger) : EthUnsigned(value, 8)
class EthUint16(value: BigInteger) : EthUnsigned(value, 16)
class EthUint32(value: BigInteger) : EthUnsigned(value, 32)
class EthUint64(value: BigInteger) : EthUnsigned(value, 64)
class EthUint112(value: BigInteger) : EthUnsigned(value, 112)
class EthUint128(value: BigInteger) : EthUnsigned(value, 128)
class EthUint160(value: BigInteger) : EthUnsigned(value, 160)
class EthUint256(value: BigInteger) : EthUnsigned(value, 256)