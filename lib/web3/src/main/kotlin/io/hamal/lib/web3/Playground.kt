package io.hamal.lib.web3

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import java.math.BigInteger

fun main() {
    val service = EthHttpBatchService()
        .getLiteBlock(EthHash(EthPrefixedHexString("0x20a78f3e977c5a4beec8b2b16c108a2ed48882205996338b3c03ce98477501c3")))
        .getLiteBlock(EthUint64(BigInteger.valueOf(17654293)))

    service.execute().forEach {
        println(it)
    }

}