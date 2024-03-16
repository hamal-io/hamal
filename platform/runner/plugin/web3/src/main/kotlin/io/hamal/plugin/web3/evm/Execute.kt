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
                                            blockResult["hash"] = response.result?.hash?.toPrefixedHexString()?.value?.let(::KuaString)!!
                                            blockResult["parent_hash"] = response.result?.parentHash?.toPrefixedHexString()?.value?.let(::KuaString)!!
                                            blockResult["gas_used"] = response.result?.gasUsed?.toPrefixedHexString()?.value?.let(::KuaString)!!
                                            blockResult["gas_limit"] = response.result?.gasLimit?.toPrefixedHexString()?.value?.let(::KuaString)!!
                                        }
                                    }
                                )
                            }

                            else -> TODO()
                        }
                    }
                }
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            return KuaError(t.message ?: "Unknown error") to null
        }
    }
}