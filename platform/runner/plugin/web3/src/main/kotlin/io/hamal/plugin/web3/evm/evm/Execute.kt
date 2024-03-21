package io.hamal.plugin.web3.evm.evm

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
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmHotGetBlockResponse
import io.hamal.lib.web3.evm.http.EvmHotHttpBatchService

private val log = logger(EvmExecuteFunction::class)

class EvmExecuteFunction : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        try {

            val url = arg1.getString("url")
//            val batchService = EthHttpBatchService(HttpTemplateImpl(url.stringValue))
            val batchService = EvmHotHttpBatchService(HttpTemplateImpl(url.stringValue))

            arg1.getTable("requests").also { requestsTable ->
                ctx.checkpoint {
                    ctx.nilPush()

                    while (ctx.tableNext(requestsTable.index).booleanValue) {
                        val request = ctx.tableGet(ctx.absIndex(-1))
                        ctx.checkpoint {
                            when (request.getString("type")) {
                                KuaString("get_block") -> {

                                    val block = when (request.type("block")) {
                                        KuaNumber::class -> batchService.getBlock(EvmUint64(request.getLong("block")))
                                        //FIXME support get by hash too
                                        KuaString::class -> {
                                            val block = request.getString("block").stringValue
                                            if (block.isDecimal()) {
                                                batchService.getBlock(EvmUint64(block.toBigInteger()))
                                            } else {
                                                batchService.getBlock(EvmUint64(EvmPrefixedHexString(block)))
                                            }
                                        }

                                        else -> TODO()
                                    }
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
                            is EvmHotGetBlockResponse -> {
                                result.append(
                                    ctx.tableCreate().also { result ->
                                        result["id"] = KuaString(response.id.value)
                                        result["result"] = response.result.toKuaSnakeCase(ctx)
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