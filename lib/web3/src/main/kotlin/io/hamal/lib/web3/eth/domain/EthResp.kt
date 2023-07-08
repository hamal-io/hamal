package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthUint64
import kotlinx.serialization.Serializable

@Serializable
sealed interface EthResp

@Serializable
data class EthResponse(
    val result: EthBlock
)

@Serializable
data class EthGetBlockResponse(
    val result: EthBlock
)

@Serializable
class BlockNumberResp(
    val requestId: EthReqId,
    val value: EthUint64
) : EthResp {
    override fun toString(): String {
        return "EthBlockNumber($value)"
    }
}
