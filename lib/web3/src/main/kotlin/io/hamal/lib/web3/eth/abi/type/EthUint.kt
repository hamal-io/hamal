package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.Web3Formatter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

@Serializable
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

internal abstract class EthUnsignedSerializer<UNSIGNED : EthUnsigned>(
    val fn: (BigInteger) -> UNSIGNED
) : KSerializer<UNSIGNED> {
    override val descriptor = PrimitiveSerialDescriptor("EthUnsigned", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): UNSIGNED {
        return fn(BigInteger(decoder.decodeString().replace("x", ""), 16))
    }

    override fun serialize(encoder: Encoder, value: UNSIGNED) {
        encoder.encodeString(value.value.toString(16))
    }
}


@Serializable(with = EthUint8.Serializer::class)
data class EthUint8(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 8

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint8>(::EthUint8)
}

@Serializable(with = EthUint16.Serializer::class)
data class EthUint16(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 16

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint16>(::EthUint16)
}

@Serializable(with = EthUint32.Serializer::class)
data class EthUint32(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 32

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint32>(::EthUint32)
}

@Serializable(with = EthUint64.Serializer::class)
data class EthUint64(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 64

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint64>(::EthUint64)
}

@Serializable(with = EthUint112.Serializer::class)
data class EthUint112(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 112

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint112>(::EthUint112)
}

@Serializable(with = EthUint128.Serializer::class)
data class EthUint128(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 128

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint128>(::EthUint128)
}

@Serializable(with = EthUint160.Serializer::class)
data class EthUint160(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 160

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint160>(::EthUint160)
}

@Serializable(with = EthUint256.Serializer::class)
data class EthUint256(override val value: BigInteger) : EthUnsigned() {
    override val numberOfBits = 256

    init {
        ensureValidValue()
    }

    internal object Serializer : EthUnsignedSerializer<EthUint256>(::EthUint256)
}