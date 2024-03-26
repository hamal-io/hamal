package io.hamal.lib.web3.evm.domain

import io.hamal.lib.common.hot.HotObject

interface EvmResponse {
    val jsonrpc: String
    val id: EvmRequestId
}

sealed class EvmHotResponse : EvmResponse {
    override val jsonrpc: String = "2.0"
}

data class EvmHotGetBlockResponse(
    override val id: EvmRequestId,
    val result: HotObject?
) : EvmHotResponse() {
    override fun toString(): String {
        return "GenericGetBlockResponse($result)"
    }
}
