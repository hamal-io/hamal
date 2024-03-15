package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.Web3Formatter

data class EthHash(
    override val value: EthBytes32
) : EthType<EthBytes32> {
    constructor(byteArray: ByteArray) : this(EthBytes32(byteArray))
    constructor(prefixedHexString: EthPrefixedHexString) : this(EthBytes32(prefixedHexString))
    constructor(prefixedHexString: String) : this(EthBytes32(EthPrefixedHexString(prefixedHexString)))

    override fun toByteArray() = value.toByteArray()

    override fun toByteWindow() = value.toByteWindow()

    fun toHexString(): EthHexString {
        return EthHexString(Web3Formatter.formatToHex(value.value))
    }

    fun toPrefixedHexString(): EthPrefixedHexString {
        return EthPrefixedHexString("0x${Web3Formatter.formatToHex(value.value)}")
    }

    override fun toString() = toPrefixedHexString().value
}