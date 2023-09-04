package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.Web3Formatter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = EthHash.Serializer::class)
data class EthHash(
    override val value: EthBytes32
) : EthType<EthBytes32> {
    constructor(byteArray: ByteArray) : this(EthBytes32(byteArray))
    constructor(prefixedHexString: EthPrefixedHexString) : this(EthBytes32(prefixedHexString))
    constructor(prefixedHexString: String) : this(EthBytes32(EthPrefixedHexString(prefixedHexString)))

    override fun toByteArray() = value.toByteArray()

    override fun toByteWindow() = value.toByteWindow()

    fun toHexString(): EthHexString {
        return EthHexString(Web3Formatter.formatToHex(value.value))
    }

    fun toPrefixedHexString(): EthPrefixedHexString {
        return EthPrefixedHexString("0x${Web3Formatter.formatToHex(value.value)}")
    }

    override fun toString() = toPrefixedHexString().value

    object Serializer : KSerializer<EthHash> {
        override val descriptor = PrimitiveSerialDescriptor("EthHash", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): EthHash {
            return EthHash(EthPrefixedHexString(decoder.decodeString()))
        }

        override fun serialize(encoder: Encoder, value: EthHash) {
            encoder.encodeString(value.toPrefixedHexString().value)
        }
    }



}