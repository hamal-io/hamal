package io.hamal.lib.web3.evm.impl.arbitrum.http

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.HttpBaseBatchService
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.json

interface ArbitrumBatchService<SERVICE : EvmBatchService<ArbitrumResponse, SERVICE>> : EvmBatchService<ArbitrumResponse, SERVICE>

class ArbitrumHttpBatchService(
    httpTemplate: HttpTemplate,
) : ArbitrumBatchService<ArbitrumHttpBatchService>, HttpBaseBatchService<ArbitrumResponse>(httpTemplate, json) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = HotArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = ArbitrumGetBlockResponse::class
        )
    }
}
