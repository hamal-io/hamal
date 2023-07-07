package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Formatter

data class EthHash(
    override val value: EthBytes32
) : EthType<EthBytes32> {
    constructor(prefixedHexString: EthPrefixedHexString) : this(EthBytes32(prefixedHexString))

    override fun toByteArray(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun toByteWindow(): ByteWindow {
        TODO("Not yet implemented")
    }

    fun toHexString(): EthHexString {
        return EthHexString(Web3Formatter.formatToHex(value.value))
    }
}