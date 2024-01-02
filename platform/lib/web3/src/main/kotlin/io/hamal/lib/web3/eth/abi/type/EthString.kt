package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Parser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.charset.Charset

sealed interface EthBaseString : EthType<String> {
    operator fun get(idx: Int): Char = value[idx]

    fun lowercase(): String = value.lowercase()

    fun filter(predicate: (Char) -> Boolean): String {
        return value.filter(predicate)
    }

    fun all(predicate: (Char) -> Boolean): Boolean {
        return value.all(predicate)
    }
}

data class EthString(
    override val value: String
) : EthBaseString {
    override fun toByteArray(): ByteArray = value.toByteArray()
    override fun toByteWindow() = ByteWindow.of(toByteArray())
    override fun toString(): String = value
}

data class EthHexString(
    override val value: String
) : EthBaseString {

    init {
        ValidateHexString(value)
    }

    override fun toByteArray(): ByteArray = Web3Parser.parseHex(value)
    override fun toByteWindow() = ByteWindow.of(this)
    override fun toString(): String = value
}

data class EthPrefixedHexString(
    override val value: String
) : EthBaseString {
    constructor(byteArray: ByteArray) : this(String(byteArray, Charset.forName("UTF-8")))

    init {
        require(value.startsWith("0x")) { "$value does not start with 0x" }
        ValidateHexString(value.substring(2))
    }

    fun toHexString(): EthHexString = EthHexString(value.substring(2))
    override fun toByteArray(): ByteArray = toHexString().toByteArray()
    override fun toByteWindow() = ByteWindow.of(this)
    override fun toString(): String = value

    object Serializer : KSerializer<EthPrefixedHexString> {
        override val descriptor = PrimitiveSerialDescriptor("EthPrefixedHexString", PrimitiveKind.STRING)
        override fun deserialize(decoder: Decoder) = EthPrefixedHexString(decoder.decodeString())
        override fun serialize(encoder: Encoder, value: EthPrefixedHexString) {
            encoder.encodeString(value.value)
        }
    }
}


internal object ValidateHexString {
    operator fun invoke(hexCandidate: String) {
        require(hexPattern.find(hexCandidate) != null) { "$hexCandidate does not match hex pattern" }

    }

    private val hexPattern = Regex("""^[0-9a-fA-F]*$""")
}