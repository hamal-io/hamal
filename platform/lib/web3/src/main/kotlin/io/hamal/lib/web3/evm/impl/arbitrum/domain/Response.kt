package io.hamal.lib.web3.evm.impl.arbitrum.domain

import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.domain.EvmResponse

sealed class ArbitrumResponse : EvmResponse {
    override val jsonrpc: String = "2.0"
}

data class ArbitrumGetBlockResponse(
    val id: EvmRequestId,
    val result: ArbitrumBlock?
) : ArbitrumResponse() {
    override fun toString(): String {
        return "ArbitrumGetBlockResponse($result)"
    }
}

data class ArbitrumCallResponse(
    val id: EvmRequestId,
    val result: EvmPrefixedHexString?
) : ArbitrumResponse() {
    override fun toString(): String {
        return "ArbitrumCallResponse($result)"
    }
}

data class ArbitrumErrorResponse(
    val id: EvmRequestId,
    val error: ArbitrumError
) : ArbitrumResponse() {
    override fun toString(): String {
        return "ArbitrumError($error)"
    }
}