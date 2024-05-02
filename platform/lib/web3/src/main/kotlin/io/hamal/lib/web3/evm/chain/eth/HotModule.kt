package io.hamal.lib.web3.evm.chain.eth

import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.eth.domain.EthRequest

object EthHotModule : SerializationModule() {
    init {
        set(EthRequest::class, EthRequest.Adapter)
        set(EthGetBlockByNumberRequest::class, EthGetBlockByNumberRequest.Adapter)
    }
}
