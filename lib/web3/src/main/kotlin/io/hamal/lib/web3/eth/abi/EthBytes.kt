package io.hamal.lib.web3.eth.abi

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
}

class EthBytes32(
    value: ByteArray
) : EthBytes(value, 32)


/**
 * Intended to be used in unit tests only
 */
internal class TestEthBytes(bytes: ByteArray, numberOfBytes: Int) : EthBytes(bytes, numberOfBytes)