package io.hamal.lib.web3.evm.http

import io.hamal.lib.common.serialization.serde.SerdeArray
import io.hamal.lib.common.serialization.serde.SerdeNull
import io.hamal.lib.common.serialization.serde.SerdeObject
import io.hamal.lib.common.serialization.serde.SerdeString
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.evm.EvmBatchService
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmHotCallResponse
import io.hamal.lib.web3.evm.domain.EvmHotGetBlockResponse
import io.hamal.lib.web3.evm.domain.EvmHotResponse
import io.hamal.lib.web3.evm.domain.EvmHotSendRawTransactionResponse
import io.hamal.lib.web3.json

interface EvmHotBatchService<SERVICE : EvmBatchService<EvmHotResponse, SERVICE>> :
    EvmBatchService<EvmHotResponse, SERVICE>

class EvmHotHttpBatchService(
    httpTemplate: HttpTemplate,
) : EvmHotBatchService<EvmHotHttpBatchService>, HttpBaseBatchService<EvmHotResponse>(httpTemplate, json) {

    override fun getBlock(number: EvmUint64) = also {
        request(
            method = "eth_getBlockByNumber",
            params = SerdeArray.builder()
                .append(number.toPrefixedHexString().value)
                .append(true)
                .build(),
            resultClass = EvmHotGetBlockResponse::class
        )
    }

    override fun call(to: EvmAddress, data: EvmPrefixedHexString, number: EvmUint64, from: EvmAddress?) = also {
        request(
            method = "eth_call",
            params = SerdeArray.builder()
                .append(
                    SerdeObject.builder()
                        .set("from", from?.toPrefixedHexString()?.value?.let(::SerdeString) ?: SerdeNull)
                        .set("to", to.toPrefixedHexString().value)
                        .set("data", data.value)
                        .build()
                )
                .append(SerdeString(number.toPrefixedHexString().value))
                .build(),
            resultClass = EvmHotCallResponse::class
        )
    }

    override fun sendRawTransaction(data: EvmPrefixedHexString) = also {
        request(
            method = "eth_sendRawTransaction",
            params = SerdeArray.builder().append(data.toString()).build(),
            resultClass = EvmHotSendRawTransactionResponse::class
        )
    }
}
