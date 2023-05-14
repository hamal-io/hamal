package io.hamal.lib.web3.eth

import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

interface EthService {
    fun getBlock(): Response
}

// local log = require("sys/log")
// local eth = require("web3/eth") -- extension/module
// eth['url'] = 'https://cloudflare-eth.com'
// local block = eth.get_block(0x1234)
// log.info(block)

object DefaultEthService : EthService {
    override fun getBlock(): Response {

        return HttpTemplate("https://cloudflare-eth.com")
            .post("/")
            .body(
                """
                {
                	"jsonrpc":"2.0",
                	"method":"eth_getBlockByNumber",
                	"params":[
                		"latest", 
                		true
                	],
                	"id":1
                }
            """.trimIndent()
            )
            .execute(Response::class)
    }
}


//fun main() {
//    val response = DefaultEthService.getBlock()
//    println(response)
//}


@Serializable
data class Response(
    val result: Block
)

@Serializable
data class Block(
    val baseFeePerGas: String,
    val extraData: String,
    val hash: String
)