package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.value.*

class Web3Extension : Extension {
    override fun create(): EnvValue {
        val ethEnvironment = EnvValue(
            ident = IdentValue("eth"),
            values = TableValue(
                "block" to TableValue(
                    "get" to fn
                )
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
//
//                    //FIXME it would be cool if application can extend type system -
//                    // so that web3 entities are accessible in script as well
//                    return TableValue(
//                        StringValue("number") to NumberValue(block.number.value),
//                        StringValue("miner") to StringValue(block.miner.toPrefixedHexString().toString()),
//                        StringValue("hash") to StringValue(block.hash.toString()),
//                        StringValue("parentHash") to StringValue(block.parentHash.toString())
//                    )

//            val response = DefaultEthService.getBlock()

            return TableValue(
//                "hash" to StringValue(response.result.hash)
            )
        }

    }

}