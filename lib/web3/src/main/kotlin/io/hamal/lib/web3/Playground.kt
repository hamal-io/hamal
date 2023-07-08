package io.hamal.lib.web3

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

fun main() {
    val a = JsonArray(listOf(JsonPrimitive("dads")))


    println(Json {  }.encodeToString(a))


//    val response = HttpTemplate("https://cloudflare-eth.com")
//        .post("/")
//        .body(
//            """
//                {
//                	"jsonrpc":"2.0",
//                	"method":"eth_getBlockByNumber",
//                	"params":[
//                		"latest",
//                		true
//                	],
//                	"id":1
//                }
//            """.trimIndent()
//        )
//        .execute(EthGetBlockResponse::class)
//
//    println(response)

}