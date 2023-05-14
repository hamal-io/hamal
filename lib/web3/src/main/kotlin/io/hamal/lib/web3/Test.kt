package io.hamal.lib.web3

import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.EthBytes32
import io.hamal.lib.web3.eth.abi.EthHash
import io.hamal.lib.web3.eth.http.EthHttpBatchService

fun main(){
    val bs = EthHttpBatchService()

    bs.getBlock(req = EthBatchService.GetBlockByHashRequest(EthHash(EthBytes32(ByteArray(32)))))

    val result = bs.execute()

    println(result)

}