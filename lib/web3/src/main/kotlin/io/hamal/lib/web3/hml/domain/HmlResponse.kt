package io.hamal.lib.web3.hml.domain

import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import kotlinx.serialization.Serializable


@Serializable
sealed interface HmlResponse {
    val id: HmlRequestId
}

@Serializable
data class HmlGetBlockResponse(
    override val id: HmlRequestId,
    val result: HmlBlock
) : HmlResponse {
    override fun toString(): String {
        return "HmlGetBlockResponse($result)"
    }
}

@Serializable
data class HmlCallResponse(
    override val id: HmlRequestId,
    val result: EthPrefixedHexString
) : HmlResponse {
    override fun toString(): String {
        return "HmlCallResponse($result)"
    }
}