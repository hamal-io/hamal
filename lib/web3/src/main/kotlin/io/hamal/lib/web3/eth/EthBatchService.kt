package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.abi.type.EthHash


interface EthBatchService<SERVICE : EthBatchService<SERVICE>> {
    fun execute(): List<EthResponse>

    fun getBlock(req: GetBlockRequest): SERVICE

    sealed interface GetBlockRequest

    data class GetBlockByHashRequest(
        val hash: EthHash
    ) : GetBlockRequest
}