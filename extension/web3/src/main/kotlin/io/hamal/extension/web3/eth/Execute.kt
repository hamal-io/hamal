package io.hamal.extension.web3.eth

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResp
import io.hamal.lib.web3.eth.domain.EthGetBlockNumberResp
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.domain.EthGetLiteBlockResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService

object  o

class ExecuteFunction(
    private val config: ExtensionConfig
) : Function1In2Out<TableArrayValue, ErrorValue, TableArrayValue>(
    FunctionInput1Schema(TableArrayValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableArrayValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableArrayValue): Pair<ErrorValue?, TableArrayValue?> {
        try {
            val batchService = EthHttpBatchService(HttpTemplate((config.value["host"] as StringValue).value))
            ctx.pushNil()
            while (ctx.state.bridge.tableNext(arg1.index)) {
//                val i = ctx.state.getNumber(-2)
                val v = ctx.state.getTableMap(-1)

                when (v.getString("type")) {
                    "get_block" -> {
                        batchService.getBlock(
                            EthUint64(
                                v.getLong("block")
                            )
                        )
                    }
                }
                ctx.state.bridge.pop(1)
            }

            val result = ctx.tableCreateArray(0)
            batchService.execute().forEach { ethRes ->
                when (ethRes) {
                    is EthGetBlockNumberResp -> TODO()
                    is EthGetLiteBlockResp -> TODO()
                    is EthGetBlockResp -> {
                        val res = ctx.tableCreateMap()
                        res["id"] = ethRes.result.number.value.toLong()
                        res["hash"] = ethRes.result.hash.toPrefixedHexString().value
                        res["parent_hash"] = ethRes.result.parentHash.toPrefixedHexString().value
                        res["gas_used"] = ethRes.result.gasUsed.value.toLong()
                        res["gas_limit"] = ethRes.result.gasLimit.value.toLong()
                        result.append(res)
                    }

                    is EthCallResp -> {
                        result.append(ethRes.result.value)
                    }
                }
            }
            return null to result
        } catch (t: Throwable) {
            t.printStackTrace()
            return ErrorValue(t.message ?: "Unknown error") to null
        }
    }
}