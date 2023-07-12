package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.script.api.value.*
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService

class Web3Extension : Extension {
    override fun create(): EnvValue {
        val ethEnvironment = EnvValue(
            ident = IdentValue("eth"),
            values = TableValue(
                "get_block" to fn
            )
        )

        return EnvValue(
            ident = IdentValue("web3"),
            values = TableValue(
                "eth" to ethEnvironment
            )
        )
    }

    val fn = object : FuncValue() {
        override fun invoke(ctx: FuncContext): Value {
            println("Getting block")
//                    val bs = EthHttpBatchService()
//                    bs.getBlock(req = EthBatchService.GetBlockByHashRequest(EthHash(EthBytes32(ByteArray(32)))))
//                    val result = bs.execute()
//                    println(result)
//
//                    Thread.sleep(1000)
//
//                    val block = (result.first() as EthBlockResponse).result
//                    //FIXME it would be cool if application can extend type system -
//                    // so that web3 entities are accessible in script as well
//                    return TableValue(
//                        StringValue("number") to NumberValue(block.number.value),
//                        StringValue("miner") to StringValue(block.miner.toPrefixedHexString().toString()),
//                        StringValue("hash") to StringValue(block.hash.toString()),
//                        StringValue("parentHash") to StringValue(block.parentHash.toString())
//                    )


            val blockNumber = ctx.params.first().let { param ->
                when (val value = param.value) {
                    is NumberValue -> EthUint64(value.value.toLong())
                    else -> TODO()
                }
            }

            val response = EthHttpBatchService(HttpTemplate("http://localhost:8081"))
                .getBlock(blockNumber)
                .execute()
                .filterIsInstance<EthGetBlockResp>()
                .first()

            return TableValue(
                "parent_hash" to StringValue(response.result.parentHash.toPrefixedHexString().value),
                "hash" to StringValue(
                    response.result.hash.toPrefixedHexString().value
                ),
                "transactions" to TableValue(
                    *response.result.transactions.mapIndexed { idx, tx ->
                        NumberValue(idx + 1) to TableValue(
                            "hash" to StringValue(tx.hash.toPrefixedHexString().value)
                        )
                    }.toTypedArray()
                )
            )
        }

    }

}