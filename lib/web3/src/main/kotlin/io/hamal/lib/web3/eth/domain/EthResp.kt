package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthUint64
import kotlinx.serialization.Serializable

@Serializable
sealed interface EthResp {
    val id: EthReqId
}

@Serializable
data class EthGetBlockResp(
    override val id: EthReqId,
    val result: EthBlock
) : EthResp {
    override fun toString(): String {
        return "EthGetBlockResp($result)"
    }
}

@Serializable
data class EthGetTransactionResp(
    override val id: EthReqId,
    val result: EthTransaction
) : EthResp {
    override fun toString(): String {
        return "EthGetTransactionResp($result)"
    }
}

@Serializable
data class EthGetReceiptResp(
    override val id: EthReqId,
    val result: EthReceipt
) : EthResp {
    override fun toString(): String {
        return "EthGetReceiptResp($result)"
    }
}


@Serializable
data class EthGetLiteBlockResp(
    override val id: EthReqId,
    val result: EthLiteBlock
) : EthResp {
    override fun toString(): String {
        return "EthGetLiteBlockResp($result)"
    }
}

@Serializable
class EthGetBlockNumberResp(
    override val id: EthReqId,
    val result: EthUint64
) : EthResp {
    override fun toString(): String {
        return "EthGetBlockNumberResp($result)"
    }
}


