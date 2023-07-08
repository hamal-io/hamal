package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.domain.EthGetBlockRequest
import io.hamal.lib.web3.eth.domain.EthResp


interface EthBatchService<SERVICE : EthBatchService<SERVICE>> {
    fun execute(): List<EthResp>
    fun getBlock(req: EthGetBlockRequest): SERVICE
}