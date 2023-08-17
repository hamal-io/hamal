package io.hamal.extension.web3.eth

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableTypeArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResponse
import io.hamal.lib.web3.eth.domain.EthGetBlockNumberResponse
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.domain.EthGetLiteBlockResponse
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import logger

private val log = logger(EthExecuteFunction::class)

class EthExecuteFunction(
    private val config: ExtensionConfig
) : Function1In2Out<TableTypeArray, ErrorType, TableTypeArray>(
    FunctionInput1Schema(TableTypeArray::class),
    FunctionOutput2Schema(ErrorType::class, TableTypeArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableTypeArray): Pair<ErrorType?, TableTypeArray?> {
        try {
            log.trace("Setting up batch service")
            val batchService = EthHttpBatchService(HttpTemplate((config.value["host"] as StringType).value))
            ctx.pushNil()
            while (ctx.state.native.tableNext(arg1.index)) {
//                val i = ctx.state.getNumber(-2)
                val v = ctx.state.getTableMap(-1)

                when (v.getString("type")) {
                    "get_block" -> {
                        val block = v.getLong("block")
                        batchService.getBlock(EthUint64(block))
                        log.trace("Requesting block $block")
                    }
                }
                ctx.state.native.pop(1)
            }

            return null to batchService.execute().let {
                val result = ctx.tableCreateArray(0)
                it.forEach { ethRes ->
                    when (ethRes) {
                        is EthGetBlockNumberResponse -> TODO()
                        is EthGetLiteBlockResponse -> TODO()
                        is EthGetBlockResponse -> {
                            val res = ctx.tableCreateMap()
                            res["id"] = ethRes.result.number.value.toLong()
                            res["hash"] = ethRes.result.hash.toPrefixedHexString().value
                            res["parent_hash"] = ethRes.result.parentHash.toPrefixedHexString().value
                            res["gas_used"] = ethRes.result.gasUsed.value.toLong()
                            res["gas_limit"] = ethRes.result.gasLimit.value.toLong()
                            result.append(res)
                            log.trace("${ethRes.id} - block response: $res")
                        }

                        is EthCallResponse -> {
                            result.append(ethRes.result.value)
                            log.trace("${ethRes.id} - call response: ${ethRes.result.value}")
                        }
                    }
                }
                result
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            return ErrorType(t.message ?: "Unknown error") to null
        }
    }
}