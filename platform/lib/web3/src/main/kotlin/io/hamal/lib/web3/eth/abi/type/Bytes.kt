package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Formatter
import java.nio.ByteBuffer

sealed interface EthBytes : EthType<ByteArray> {
    val numberOfBytes: Int

    override fun toByteArray(): ByteArray = value
    override fun toByteWindow() = ByteWindow(ByteBuffer.wrap(value), numberOfBytes)
    fun toPrefixedHexString(): EthPrefixedHexString {
        return EthPrefixedHexString("0x${Web3Formatter.formatToHex(value).replaceFirst("^0{2,}(?!$)".toRegex(), "")}")
    }
}

class EthBytes32(
    override val value: ByteArray
) : EthBytes {

    constructor(prefixedHexString: String) : this(EthPrefixedHexString(prefixedHexString))
    constructor(prefixedHexString: EthPrefixedHexString) : this(prefixedHexString.toByteWindow().next())

    override val numberOfBytes = 32

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EthBytes32
        if (!value.contentEquals(other.value)) return false
        return numberOfBytes == other.numberOfBytes
    }

    override fun hashCode(): Int {
        var result = value.contentHashCode()
        result = 31 * result + numberOfBytes
        return result
    }

    override fun toString(): String {
        return toPrefixedHexString().toString()
    }
}
