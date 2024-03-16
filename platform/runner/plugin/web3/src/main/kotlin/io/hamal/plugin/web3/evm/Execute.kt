package io.hamal.plugin.web3.evm

import io.hamal.lib.common.logger
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.type.*
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.http.EthHttpBatchService

private val log = logger(EthExecuteFunction::class)

class EthExecuteFunction : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        try {
            log.trace("Setting up batch service")


            val url = arg1.getString("url")
            println(url)

            val batchService = EthHttpBatchService(HttpTemplateImpl(url.stringValue))


            arg1.getTable("requests").also { requestsTable ->
                ctx.checkpoint {
                    ctx.nilPush()

                    while (ctx.tableNext(requestsTable.index).booleanValue) {
                        val request = ctx.tableGet(ctx.absIndex(-1))
                        ctx.checkpoint {
                            when (request.getString("type")) {
                                KuaString("get_block") -> {
                                    val block = request.getLong("block")
                                    batchService.getBlock(EthUint64(block))
                                    log.debug("Requesting block $block")
                                }

                                else -> TODO()
                            }
                        }
                        ctx.topPop(1)
                    }
                }
            }

//            arg1.getTable("requests").asList().forEach { request ->
//                require(request is KuaTable)
//
//                when (request.getString("type")) {
//                    KuaString("get_block") -> {
//                        val block = request.getLong("block")
//                        batchService.getBlock(EthUint64(block))
//                        log.debug("Requesting block $block")
//                    }
//                    else -> TODO()
//                }
//            }


//            arg1.asSequence().forEach { (_, v) ->
//                require(v is KuaTable)
//
//                when (v.getString("type")) {
//                    "get_block" -> {
//                        val block = v.getLong("block")
//                        batchService.getBlock(EthUint64(block))
//                        log.trace("Requesting block $block")
//                    }
//
//                    "call" -> {
//                        val block = v.getLong("block")
//                        batchService.call(
//                            EthBatchService.EthCallRequest(
//                                to = EthAddress(EthPrefixedHexString(v.getString("to"))),
//                                data = EthPrefixedHexString(v.getString("data")),
//                                blockNumber = EthUint64(block),
//                            )
//                        )
//                        log.trace("Requesting block $block")
//                    }
//                }
//            }


            return null to batchService.execute().let {
                ctx.tableCreate(it.size, 0).also { result ->
                    it.forEach { response ->
                        when (response) {
                            is EthGetBlockResponse -> {
                                result.append(
                                    ctx.tableCreate().also { result ->
                                        result["id"] = KuaString(response.id.value)
                                        result["result"] = ctx.tableCreate().also { blockResult ->
                                            blockResult["number"] = response.result?.number?.toPrefixedHexString()?.value?.let(::KuaString)!!
                                        }
                                    }
                                )
                            }

                            else -> TODO()
                        }
                    }
                }

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