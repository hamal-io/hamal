package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Formatter
import io.hamal.lib.web3.util.Web3HashUtils
import java.lang.Character.isLowerCase
import java.lang.Character.isUpperCase
import java.math.BigInteger
import java.util.*


data class EvmAddress(
    override val value: EvmUint160
) : EvmType<EvmUint160> {
    constructor(value: BigInteger) : this(EvmUint160(value))
    constructor(prefixedHexString: String) : this(EvmPrefixedHexString(prefixedHexString).toHexString())
    constructor(hexString: EvmPrefixedHexString) : this(hexString.toHexString())
    constructor(hexString: EvmHexString) : this(BigInteger(hexString.value, 16))

    companion object {
        @JvmStatic
        val Zero: EvmAddress = EvmAddress(BigInteger.ZERO)

        @JvmStatic
        val Null: EvmAddress = EvmAddress(BigInteger("1158896792795502070752211396329834747757200325310"))

        @JvmStatic
        fun of(hexString: EvmHexString): EvmAddress {
            ValidateAddress(hexString)
            return EvmAddress(BigInteger(hexString.value, 16))
        }

        @JvmStatic
        fun of(prefixedHexString: EvmPrefixedHexString) = of(prefixedHexString.toHexString())
    }

    fun toPrefixedHexString(): EvmPrefixedHexString {
        return if (this == Null || this == Zero) {
            EvmPrefixedHexString("0x0000000000000000000000000000000000000000")
        } else {
            EvmPrefixedHexString(
                "0x" + encodeChecksum(Web3Formatter.formatFixLength(value.toByteArray(), 40))
            )
        }
    }

    override fun toByteArray(): ByteArray = toPrefixedHexString().toByteArray()

    override fun toByteWindow(): ByteWindow = toPrefixedHexString().toByteWindow()

    override fun toString() = toPrefixedHexString().toString()
}

internal object ValidateAddress {
    operator fun invoke(addressCandidate: EvmHexString) {
        require(addressPattern.find(addressCandidate.value) != null) { "$addressCandidate does not match address pattern" }
        require(matchesChecksum(addressCandidate)) { "$addressCandidate does not match checksum encoding" }
    }

    private fun matchesChecksum(addressCandidate: EvmHexString): Boolean {
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