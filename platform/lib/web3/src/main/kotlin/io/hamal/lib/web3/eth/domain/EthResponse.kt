package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.*

sealed interface EthResponse {
    val id: EthRequestId
}

data class EthGetBlockResponse(
    override val id: EthRequestId,
//    val result: EthBlock
    val result: DummyBlock
) : EthResponse {
    override fun toString(): String {
        return "EthGetBlockResponse($result)"
    }
}

data class DummyBlock(
    val number: EthUint64,
    val hash: EthHash,
    val parentHash: EthHash,
    val sha3Uncles: EthHash,
    val miner: EthAddress,
    val stateRoot: EthHash,
    val transactionsRoot: EthHash,
    val receiptsRoot: EthHash,
    val gasLimit: EthUint64,
    val gasUsed: EthUint64,
    val timestamp: EthUint64,
//    val extraData: EthBytes32,
    val transactions: List<Transaction>
) {
    data class Transaction(
        val type: EthUint8,
        val hash: EthHash,
        val from: EthAddress,
        val to: EthAddress?,
        val input: EthPrefixedHexString,
        val value: EthUint256,
        val gas: EthUint64,
        val gasPrice: EthUint64,
    )
}


data class EthGetLiteBlockResponse(
    override val id: EthRequestId,
    val result: EthLiteBlock
) : EthResponse {
    override fun toString(): String {
        return "EthGetLiteBlockResponse($result)"
    }
}

class EthGetBlockNumberResponse(
    override val id: EthRequestId,
    val result: EthUint64
) : EthResponse {
    override fun toString(): String {
        return "EthGetBlockNumberResponse($result)"
    }
}


data class EthCallResponse(
    override val id: EthRequestId,
    val result: EthPrefixedHexString
) : EthResponse {
    override fun toString(): String {
        return "EthCallResponse($result)"
    }
}