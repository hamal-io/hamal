package io.hamal.worker.extension.web3

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.NativeEnvironment
import io.hamal.lib.script.api.native_.NativeFunction
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.web3.eth.DefaultEthService
import io.hamal.worker.extension.api.WorkerExtension

class Web3Extension : WorkerExtension {
    override fun functionFactories(): List<NativeFunction> {
        return listOf(
            fn
        )
    }

    override fun environments(): List<Environment> {
        return listOf(
            NativeEnvironment(
                Identifier("eth"),
                mapOf(
                    Identifier("getBlock") to fn
                )
            )
        )
    }

    val fn = object : NativeFunction {
        override val identifier = Identifier("getBlock")

        override fun invoke(ctx: NativeFunction.Context): Value {
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

            val response = DefaultEthService.getBlock()

            return TableValue(
                StringValue("hash") to StringValue(response.result.hash)
            )
        }
    }
}