package io.hamal.agent.extension.web3

import io.hamal.agent.extension.api.Extension
import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.FunctionValue
import io.hamal.lib.script.api.NativeEnvironment
import io.hamal.lib.script.api.value.*
import io.hamal.lib.web3.eth.DefaultEthService

class Web3Extension : Extension {
    override fun create(): Environment {
        val ethEnvironment = NativeEnvironment(
            identifier = Identifier("eth"),
            values = mapOf(
                Identifier("getBlock") to fn
            )
        )

        return NativeEnvironment(
            identifier = Identifier("web3"),
            values = mapOf(
                Identifier("eth") to ethEnvironment
            )
        )
    }

    val fn = object : FunctionValue {
        override val metaTable = MetaTableNotImplementedYet

        override val identifier = Identifier("getBlock")

        override fun invoke(ctx: FunctionValue.Context): Value {
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