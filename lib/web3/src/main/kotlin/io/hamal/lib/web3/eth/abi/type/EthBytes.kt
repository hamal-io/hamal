package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow

sealed class EthBytes(
    final override val value: ByteArray,
    numberOfBytes: Int
) : EthType<ByteArray> {

    init {
        require(value.size == numberOfBytes) { "Requires array of $numberOfBytes bytes" }
    }

    override fun toByteArray(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun toByteWindow(): ByteWindow {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as EthBytes
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

class EthBytes32(
    value: ByteArray
) : EthBytes(value, 32) {
    constructor(prefixedHexString: EthPrefixedHexString) : this(prefixedHexString.toByteWindow().next())

}


/**
 * Intended to be used in unit tests only
 */
internal class TestEthBytes(bytes: ByteArray, numberOfBytes: Int) : EthBytes(bytes, numberOfBytes)