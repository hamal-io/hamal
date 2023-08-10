package io.hamal.extension.web3.hml

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.hml.domain.HmlCallResponse
import io.hamal.lib.web3.hml.domain.HmlGetBlockResponse
import io.hamal.lib.web3.hml.http.HmlHttpBatchService


class HmlExecuteFunction(
    private val config: ExtensionConfig
) : Function1In2Out<TableArrayValue, ErrorValue, TableArrayValue>(
    FunctionInput1Schema(TableArrayValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableArrayValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableArrayValue): Pair<ErrorValue?, TableArrayValue?> {
        try {
            val batchService = HmlHttpBatchService(HttpTemplate((config.value["host"] as StringValue).value))
            ctx.pushNil()
            while (ctx.state.native.tableNext(arg1.index)) {
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
                ctx.state.native.pop(1)
            }

            val result = ctx.tableCreateArray(0)
            batchService.execute().forEach { r ->
                when (r) {
                    is HmlGetBlockResponse -> {
                        val res = ctx.tableCreateMap()
                        res["id"] = r.result.number.value.toLong()
                        res["hash"] = r.result.hash.toPrefixedHexString().value
                        res["gas_used"] = r.result.gasUsed.value.toLong()
                        res["gas_limit"] = r.result.gasLimit.value.toLong()

                        val txs = ctx.tableCreateArray(r.result.transactions.size)
                        r.result.transactions.forEach {
                            val tx = ctx.tableCreateMap()
                            tx["type"] = NumberValue(it.type.value.toDouble())
                            tx["from"] = StringValue(it.from.toPrefixedHexString().value)
                            tx["fromId"] = NumberValue(it.fromId.value.toDouble())
//FIXME                            tx["to"] = it.to?.let { StringValue(it.toPrefixedHexString().value) } ?: NilValue
                            tx["to"] = it.to?.let { StringValue(it.toPrefixedHexString().value) } ?: StringValue("")
                            tx["input"] = StringValue(it.input.value)
                            tx["value"] = NumberValue(it.value.value.toDouble()) // FIXME decimal value
                            tx["gas"] = NumberValue(it.gas.value.toDouble()) // FIXME decimal value
                            txs.append(tx)
                        }

                        res["transactions"] = txs
                        result.append(res)
                    }

                    is HmlCallResponse -> {
                        result.append(r.result.value)
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