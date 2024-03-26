package io.hamal.lib.web3.evm.impl.eth

import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.evm.impl.eth.domain.EthRequest

object EthHotModule : HotModule() {
    init {
        set(EthRequest::class, EthRequest.Adapter)
        set(EthGetBlockByNumberRequest::class, EthGetBlockByNumberRequest.Adapter)
    }
}
