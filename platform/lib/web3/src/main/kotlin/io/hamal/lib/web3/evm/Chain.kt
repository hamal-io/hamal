package io.hamal.lib.web3.evm

import io.hamal.lib.web3.evm.abi.type.EvmUint64


sealed class ChainId(val id: EvmUint64) {

    data object EthMain : ChainId(EvmUint64(1))
    data object EthSepolia : ChainId(EvmUint64(11155111))

}