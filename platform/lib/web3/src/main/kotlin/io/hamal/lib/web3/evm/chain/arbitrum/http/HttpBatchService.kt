package io.hamal.lib.web3.evm.chain.arbitrum.http

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNull
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotString
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumCallResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumResponse
import io.hamal.lib.web3.evm.http.HttpBaseBatchService
import io.hamal.lib.web3.json

interface ArbitrumBatchService<SERVICE : EvmBatchService<ArbitrumResponse, SERVICE>> :
    EvmBatchService<ArbitrumResponse, SERVICE>

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

    override fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress?) = also {
        request(
            method = "eth_call",
            params = HotArray.builder()
                .append(
                    HotObject.builder()
                        .set("from", from?.toPrefixedHexString()?.value?.let(::HotString) ?: HotNull)
                        .set("to", to.toPrefixedHexString().value)
                        .set("data", data.value)
                        .build()
                )
                .append(HotString(number.toPrefixedHexString().value))
                .build(),
            resultClass = ArbitrumCallResponse::class
        )
    }

    override fun sendRawTransaction(data: EvmPrefixedHexString): ArbitrumHttpBatchService {
        TODO("Not yet implemented")
    }
}
