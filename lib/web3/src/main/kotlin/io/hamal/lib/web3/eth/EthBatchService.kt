package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthResp


interface EthBatchService<SERVICE : EthBatchService<SERVICE>> {
    fun execute(): List<EthResp>
    fun getBlockNumber(): SERVICE
    fun getBlock(hash: EthHash): SERVICE
    fun getBlock(number: EthUint64): SERVICE
    fun getTransaction(hash: EthHash): SERVICE
    fun getLiteBlock(hash: EthHash): SERVICE
    fun getLiteBlock(number: EthUint64): SERVICE
}