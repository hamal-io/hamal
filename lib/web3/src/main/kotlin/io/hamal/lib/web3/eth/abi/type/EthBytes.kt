package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.ByteBuffer

@Serializable
sealed interface EthBytes : EthType<ByteArray> {
    val numberOfBytes: Int

    //FIXME validate input for each and add tests
//    init {
//        require(value.size == numberOfBytes) { "Requires array of $numberOfBytes bytes" }
//    }
    override fun toByteArray(): ByteArray = value
    override fun toByteWindow() = ByteWindow(ByteBuffer.wrap(value), numberOfBytes)
    fun toPrefixedHexString() = EthPrefixedHexString(value)
}

@Serializable(with = EthBytes32.Serializer::class)
data class EthBytes32(
    override val value: ByteArray
) : EthBytes {
    constructor(prefixedHexString: EthPrefixedHexString) : this(prefixedHexString.toByteWindow().next())

    override val numberOfBytes = 32

    object Serializer : KSerializer<EthBytes32> {
        override val descriptor = PrimitiveSerialDescriptor("EthBytes32", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = EthBytes32(decoder.decodeString().toByteArray())
        override fun serialize(encoder: Encoder, value: EthBytes32) {
            encoder.encodeString(String(value.value))
        }
    }
}


/**
 * Intended to be used in unit tests only
 */
internal data class TestEthBytes(
    override val value: ByteArray,
    override val numberOfBytes: Int
) : EthBytes