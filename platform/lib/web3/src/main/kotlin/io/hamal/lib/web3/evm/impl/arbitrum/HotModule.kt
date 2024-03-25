package io.hamal.lib.web3.evm.impl.arbitrum

import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumGetBlockByNumberRequest
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumRequest

object ArbitrumHotModule : HotModule() {
    init {
        set(ArbitrumRequest::class, ArbitrumRequest.Adapter)
        set(ArbitrumGetBlockByNumberRequest::class, ArbitrumGetBlockByNumberRequest.Adapter)
    }
}
