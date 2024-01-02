package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import java.nio.ByteBuffer

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

class EthBytes32(
    override val value: ByteArray
) : EthBytes {
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
}


/**
 * Intended to be used in unit tests only
 */
internal data class TestEthBytes(
    override val value: ByteArray,
    override val numberOfBytes: Int
) : EthBytes