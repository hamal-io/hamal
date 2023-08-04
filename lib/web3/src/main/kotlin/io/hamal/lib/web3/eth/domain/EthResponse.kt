package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import kotlinx.serialization.Serializable

@Serializable
sealed interface EthResponse {
    val id: EthRequestId
}

@Serializable
data class EthGetBlockResponse(
    override val id: EthRequestId,
    val result: EthBlock
) : EthResponse {
    override fun toString(): String {
        return "EthGetBlockResponse($result)"
    }
}

@Serializable
data class EthGetLiteBlockResponse(
    override val id: EthRequestId,
    val result: EthLiteBlock
) : EthResponse {
    override fun toString(): String {
        return "EthGetLiteBlockResponse($result)"
    }
}

@Serializable
class EthGetBlockNumberResponse(
    override val id: EthRequestId,
    val result: EthUint64
) : EthResponse {
    override fun toString(): String {
        return "EthGetBlockNumberResponse($result)"
    }
}


@Serializable
data class EthCallResponse(
    override val id: EthRequestId,
    val result: EthPrefixedHexString
) : EthResponse {
    override fun toString(): String {
        return "EthCallResponse($result)"
    }
}