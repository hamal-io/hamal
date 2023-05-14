package io.hamal.worker.extension.web3

import io.hamal.lib.script.api.natives.NativeFunction
import io.hamal.lib.script.api.natives.NativeIdentifier
import io.hamal.lib.script.api.value.StringValue
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.script.api.value.Value
import io.hamal.lib.web3.eth.DefaultEthService
import io.hamal.worker.extension.api.WorkerExtension

class Web3Extension : WorkerExtension {
    override fun functionFactories(): List<NativeFunction> {
        return listOf(
            object : NativeFunction {
                override val identifier = NativeIdentifier("getBlock")

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
        )
    }
}