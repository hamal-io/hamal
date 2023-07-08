package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Formatter
import io.hamal.lib.web3.util.Web3HashUtils
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.lang.Character.isLowerCase
import java.lang.Character.isUpperCase
import java.math.BigInteger
import java.util.*

@Serializable(with = EthAddress.Serializer::class)
data class EthAddress(
    override val value: EthUint160
) : EthType<EthUint160> {

    constructor(value: BigInteger) : this(EthUint160(value))
    constructor(hexString: EthPrefixedHexString) : this(hexString.toHexString())
    constructor(hexString: EthHexString) : this(BigInteger(hexString.value, 16))

    companion object {
        @JvmStatic
        val Zero: EthAddress = EthAddress(BigInteger.ZERO)

        @JvmStatic
        val Null: EthAddress = EthAddress(BigInteger("1158896792795502070752211396329834747757200325310"))

        @JvmStatic
        fun of(hexString: EthHexString): EthAddress {
            ValidateAddress(hexString)
            return EthAddress(BigInteger(hexString.value, 16))
        }

        @JvmStatic
        fun of(prefixedHexString: EthPrefixedHexString) = of(prefixedHexString.toHexString())
    }

    fun toPrefixedHexString(): EthPrefixedHexString {
        return if (this == Null || this == Zero) {
            EthPrefixedHexString("0x0000000000000000000000000000000000000000")
        } else {
            EthPrefixedHexString(
                "0x" + encodeChecksum(Web3Formatter.formatFixLength(value.toByteArray(), 40))
            )
        }
    }

    override fun toByteArray(): ByteArray = toPrefixedHexString().toByteArray()

    override fun toByteWindow(): ByteWindow = toPrefixedHexString().toByteWindow()

    override fun toString() = toPrefixedHexString().toString()

    object Serializer : KSerializer<EthAddress> {
        override val descriptor = PrimitiveSerialDescriptor("EthAddress", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): EthAddress {
            return EthAddress(EthPrefixedHexString(decoder.decodeString()))
        }

        override fun serialize(encoder: Encoder, value: EthAddress) {
            encoder.encodeString(value.toPrefixedHexString().value)
        }
    }
}

internal object ValidateAddress {
    operator fun invoke(addressCandidate: EthHexString) {
        require(addressPattern.find(addressCandidate.value) != null) { "$addressCandidate does not match address pattern" }
        require(matchesChecksum(addressCandidate)) { "$addressCandidate does not match checksum encoding" }
    }

    private fun matchesChecksum(addressCandidate: EthHexString): Boolean {
        val allLowercase = addressCandidate.all {
            when (it.isLetter()) {
                true -> it.isLowerCase()
                false -> true
            }
        }
        val allUppercase = addressCandidate.all {
            when (it.isLetter()) {
                true -> it.isUpperCase()
                false -> true
            }
        }


        if (allUppercase || allLowercase) {
            // there is no checksum encoded - therefore we must assume it is valid
            return true
        }

        val addressHash = Web3Formatter.formatToHex(
            Web3HashUtils.keccak256(addressCandidate.lowercase().toByteArray())
        )

        for (idx in 0..39) {
            val currentAddressChar: Char = addressCandidate[idx]
            val currentAddressHashChar: String = addressHash[idx].toString()
            val addressHashDigit = currentAddressHashChar.toInt(16)
            if (addressHashDigit > 7 && isLowerCase(currentAddressChar)) {
                return false
            }
            if (addressHashDigit <= 7 && isUpperCase(currentAddressChar)) {
                return false
            }
        }
        return true
    }

    private val addressPattern = Regex("""^[0-9a-fA-F]{40}$""")
}


private fun encodeChecksum(address: String): String {
    val builder = StringBuilder()
    val hash = Web3Formatter.formatToHex(
        Web3HashUtils.keccak256(address.lowercase(Locale.getDefault()).toByteArray())
    )
    for (idx in 0..39) {
        val currentAddressChar = address[idx]
        if (Character.isDigit(currentAddressChar)) {
            builder.append(currentAddressChar)
        } else {
            val currentAddressHashChar: String = hash[idx].toString()
            val addressHashDigit = currentAddressHashChar.toInt(16)
            if (addressHashDigit > 7 && isLowerCase(currentAddressChar)) {
                builder.append(currentAddressChar.uppercaseChar())
            } else {
                builder.append(currentAddressChar.lowercaseChar())
            }
        }
    }
    return builder.toString()
}