package io.hamal.lib.web3

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.http.EthHttpBatchService

fun main() {
    val service = EthHttpBatchService(HttpTemplate("https://cloudflare-eth.com"))
//        .getLiteBlock(EthHash(EthPrefixedHexString("0x20a78f3e977c5a4beec8b2b16c108a2ed48882205996338b3c03ce98477501c3")))
//        .getLiteBlock(EthUint64(BigInteger.valueOf(17654293)))
//        .getTransaction(EthHash(EthPrefixedHexString("0x8de305f9946220bce200e7284dd8437a4a865f94b00912526a0ee978ea798686")))
        .getTransactionReceipt(EthHash(EthPrefixedHexString("0x8de305f9946220bce200e7284dd8437a4a865f94b00912526a0ee978ea798686")))

    service.execute().forEach {
        println(it)
    }

}