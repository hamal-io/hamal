package io.hamal.lib.web3.evm.chain.arbitrum

import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumRequest

object SerdeModuleJsonArbitrum : SerdeModuleJson() {
    init {
        set(ArbitrumRequest::class, ArbitrumRequest.Adapter)
        set(ArbitrumGetBlockByNumberRequest::class, ArbitrumGetBlockByNumberRequest.Adapter)
    }
}
