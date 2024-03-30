package io.hamal.lib.web3.evm.impl.eth.http

import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNull
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.hot.HotString
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.http.HttpBaseBatchService
import io.hamal.lib.web3.evm.impl.eth.domain.EthCallResponse
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.impl.eth.domain.EthResponse
import io.hamal.lib.web3.json

interface EthBatchService<SERVICE : EvmBatchService<EthResponse, SERVICE>> : EvmBatchService<EthResponse, SERVICE>

class EthHttpBatchService(
    httpTemplate: HttpTemplate,
) : EthBatchService<EthHttpBatchService>, HttpBaseBatchService<EthResponse>(httpTemplate, json) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = HotArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = EthGetBlockResponse::class
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
            resultClass = EthCallResponse::class
        )
    }
}
