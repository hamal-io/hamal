package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow

class EthHash(override val value: EthBytes32) : EthType<EthBytes32> {
    override fun toByteArray(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun toByteWindow(): ByteWindow {
        TODO("Not yet implemented")
    }
}