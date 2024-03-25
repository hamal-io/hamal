package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.util.Web3Formatter

data class EvmHash(
    override val value: EvmBytes32
) : EvmType<EvmBytes32> {
    constructor(byteArray: ByteArray) : this(EvmBytes32(byteArray))
    constructor(prefixedHexString: EvmPrefixedHexString) : this(EvmBytes32(prefixedHexString))
    constructor(prefixedHexString: String) : this(EvmBytes32(EvmPrefixedHexString(prefixedHexString)))

    override fun toByteArray() = value.toByteArray()

    override fun toByteWindow() = value.toByteWindow()

    fun toHexString(): EvmHexString {
        return EvmHexString(Web3Formatter.formatToHex(value.value))
    }

    fun toPrefixedHexString(): EvmPrefixedHexString {
        return EvmPrefixedHexString("0x${Web3Formatter.formatToHex(value.value)}")
    }

    override fun toString() = toPrefixedHexString().value
}