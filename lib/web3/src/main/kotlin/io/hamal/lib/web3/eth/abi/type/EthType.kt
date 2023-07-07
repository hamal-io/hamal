package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow

sealed interface EthType<VALUE : Any> {
    val value: VALUE
    fun toByteArray(): ByteArray

    fun toByteWindow(): ByteWindow
}


