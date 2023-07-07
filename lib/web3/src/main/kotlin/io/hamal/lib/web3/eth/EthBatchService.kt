package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.domain.EthGetBlockRequest


interface EthBatchService<SERVICE : EthBatchService<SERVICE>> {
    fun execute(): List<EthResponse>
    fun getBlock(req: EthGetBlockRequest): SERVICE

}