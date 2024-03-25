package io.hamal.lib.web3.evm.impl.eth.domain

import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.domain.EvmResponse

sealed class EthResponse : EvmResponse {
    override val jsonrpc: String = "2.0"
}

data class EthGetBlockResponse(
    override val id: EvmRequestId,
    val result: EthBlock?
) : EthResponse() {
    override fun toString(): String {
        return "EthGetBlockResponse($result)"
    }
}

data class EthErrorResponse(
    override val id: EvmRequestId,
    val error: EthError
) : EthResponse() {
    override fun toString(): String {
        return "EthError($error)"
    }
}


data class EthGetLiteBlockResponse(
    override val id: EvmRequestId,
    val result: EthLiteBlock
) : EthResponse() {
    override fun toString(): String {
        return "EthGetLiteBlockResponse($result)"
    }
}

class EthGetBlockNumberResponse(
    override val id: EvmRequestId,
    val result: EvmUint64
) : EthResponse() {
    override fun toString(): String {
        return "EthGetBlockNumberResponse($result)"
    }
}


data class EthCallResponse(
    override val id: EvmRequestId,
    val result: EvmPrefixedHexString
) : EthResponse() {
    override fun toString(): String {
        return "EthCallResponse($result)"
    }
}