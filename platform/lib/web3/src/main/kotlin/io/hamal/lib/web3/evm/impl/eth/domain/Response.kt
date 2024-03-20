package io.hamal.lib.web3.evm.impl.eth.domain

import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64

sealed class EvmResponse {
    val jsonrpc: String = "2.0"
    abstract val id: EvmRequestId
}


data class EthGetBlockResponse(
    override val id: EvmRequestId,
    val result: EthBlock?
) : EvmResponse() {
    override fun toString(): String {
        return "EthGetBlockResponse($result)"
    }
}

data class EthErrorResponse(
    override val id: EvmRequestId,
    val error: EthError
) : EvmResponse() {
    override fun toString(): String {
        return "EthError($error)"
    }
}


data class EthGetLiteBlockResponse(
    override val id: EvmRequestId,
    val result: EthLiteBlock
) : EvmResponse() {
    override fun toString(): String {
        return "EthGetLiteBlockResponse($result)"
    }
}

class EthGetBlockNumberResponse(
    override val id: EvmRequestId,
    val result: EvmUint64
) : EvmResponse() {
    override fun toString(): String {
        return "EthGetBlockNumberResponse($result)"
    }
}


data class EthCallResponse(
    override val id: EvmRequestId,
    val result: EvmPrefixedHexString
) : EvmResponse() {
    override fun toString(): String {
        return "EthCallResponse($result)"
    }
}