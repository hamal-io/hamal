package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64

sealed class EthResponse {
    val jsonrpc: String = "2.0"
    abstract val id: EthRequestId
}


data class EthGetBlockResponse(
    override val id: EthRequestId,
    val result: EthBlock?
) : EthResponse() {
    override fun toString(): String {
        return "EthGetBlockResponse($result)"
    }
}

data class EthErrorResponse(
    override val id: EthRequestId,
    val error: EthError
) : EthResponse() {
    override fun toString(): String {
        return "EthError($error)"
    }
}


data class EthGetLiteBlockResponse(
    override val id: EthRequestId,
    val result: EthLiteBlock
) : EthResponse() {
    override fun toString(): String {
        return "EthGetLiteBlockResponse($result)"
    }
}

class EthGetBlockNumberResponse(
    override val id: EthRequestId,
    val result: EthUint64
) : EthResponse() {
    override fun toString(): String {
        return "EthGetBlockNumberResponse($result)"
    }
}


data class EthCallResponse(
    override val id: EthRequestId,
    val result: EthPrefixedHexString
) : EthResponse() {
    override fun toString(): String {
        return "EthCallResponse($result)"
    }
}