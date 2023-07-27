package io.hamal.app.proxy

import io.hamal.app.proxy.repository.SqliteTransactionRepository
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.util.Web3Formatter
import kotlin.io.path.Path


fun main() {
    val txRepo = SqliteTransactionRepository(Path("/tmp/hamal/db"))

    val response = txRepo.find(17771431UL, (1).toUShort())
    println(response)


    println(String(response!!.input))

    val decoded = response.input
    println(decoded)

    val result = EthPrefixedHexString("0x" + Web3Formatter.formatToHex(decoded))
    println(result.toByteArray().size)
    println(response.input.size)
}


