package io.hamal.plugin.web3.evm

import io.hamal.lib.common.logger
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResponse
import io.hamal.lib.web3.eth.domain.EthGetBlockNumberResponse
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.domain.EthGetLiteBlockResponse
import io.hamal.lib.web3.eth.http.EthHttpBatchService

private val log = logger(EthExecuteFunction::class)

class EthExecuteFunction : Function1In2Out<KuaTable.Map, KuaError, KuaTable.Map>(
    FunctionInput1Schema(KuaTable.Map::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Map::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map): Pair<KuaError?, KuaTable.Map?> {
        try {
            log.trace("Setting up batch service")

            val batchService = EthHttpBatchService(HttpTemplateImpl("http://localhost:10001"))

            arg1.entries().forEach { (_, v) ->
                require(v is KuaTable.Map)

                when (v.getString("type")) {
                    "get_block" -> {
                        val block = v.getLong("block")
                        batchService.getBlock(EthUint64(block))
                        log.trace("Requesting block $block")
                    }

                    "call" -> {
                        val block = v.getLong("block")
                        batchService.call(
                            EthBatchService.EthCallRequest(
                                to = EthAddress(EthPrefixedHexString(v.getString("to"))),
                                data = EthPrefixedHexString(v.getString("data")),
                                blockNumber = EthUint64(block),
                            )
                        )
                        log.trace("Requesting block $block")
                    }
                }
            }


            return null to batchService.execute().let {
                TODO()
//                val result = ctx.toMap()
//                it.forEach { ethRes ->
//                    when (ethRes) {
//                        is EthGetBlockNumberResponse -> TODO()
//                        is EthGetLiteBlockResponse -> TODO()
//                        is EthGetBlockResponse -> {
//                            val res = ctx.toMap()
//                            res["number"] = ethRes.result.number.value.toLong()
//                            res["hash"] = ethRes.result.hash.toPrefixedHexString().value
//                            res["parent_hash"] = ethRes.result.parentHash.toPrefixedHexString().value
//                            res["gas_used"] = ethRes.result.gasUsed.value.toLong()
//                            res["gas_limit"] = ethRes.result.gasLimit.value.toLong()
//
////                            result.append(res)
//                            TODO()
//                            log.trace("${ethRes.id} - block response: $res")
//                        }
//
//                        is EthCallResponse -> {
////                            result.append(ethRes.result.value)
//                            TODO()
//                            log.trace("${ethRes.id} - call response: ${ethRes.result.value}")
//                        }
//                    }
//                }
//                result
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            return KuaError(t.message ?: "Unknown error") to null
        }
    }
}