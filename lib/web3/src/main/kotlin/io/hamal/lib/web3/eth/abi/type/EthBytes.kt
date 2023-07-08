package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import kotlinx.serialization.Serializable
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
}

@Serializable
data class EthBytes32(
    override val value: ByteArray
) : EthBytes {
    constructor(prefixedHexString: EthPrefixedHexString) : this(prefixedHexString.toByteWindow().next())

    override val numberOfBytes = 32
}


/**
 * Intended to be used in unit tests only
 */
internal data class TestEthBytes(
    override val value: ByteArray,
    override val numberOfBytes: Int
) : EthBytes