package io.hamal.lib.web3.evm.http

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmHotGetBlockResponse
import io.hamal.lib.web3.evm.domain.EvmHotResponse
import io.hamal.lib.web3.json

interface EvmHotBatchService<SERVICE : EvmBatchService<EvmHotResponse, SERVICE>> :
    EvmBatchService<EvmHotResponse, SERVICE>

class EvmHotHttpBatchService(
    httpTemplate: HttpTemplate,
) : EvmHotBatchService<EvmHotHttpBatchService>, HttpBaseBatchService<EvmHotResponse>(httpTemplate, json) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = HotArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = EvmHotGetBlockResponse::class
        )
    }
}
