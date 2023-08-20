package io.hamal.extension.web3.hml

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.hml.domain.HmlCallResponse
import io.hamal.lib.web3.hml.domain.HmlGetBlockResponse
import io.hamal.lib.web3.hml.http.HmlHttpBatchService


class HmlExecuteFunction(
    private val config: ExtensionConfig
) : Function1In2Out<TableProxyArray, ErrorType, TableProxyArray>(
    FunctionInput1Schema(TableProxyArray::class),
    FunctionOutput2Schema(ErrorType::class, TableProxyArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxyArray): Pair<ErrorType?, TableProxyArray?> {
        try {
            val batchService = HmlHttpBatchService(HttpTemplate((config.value["host"] as StringType).value))
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
                            tx["type"] = NumberType(it.type.value.toDouble())
                            tx["from"] = StringType(it.from.toPrefixedHexString().value)
                            tx["fromId"] = NumberType(it.fromId.value.toDouble())
//FIXME                            tx["to"] = it.to?.let { StringValue(it.toPrefixedHexString().value) } ?: NilValue
                            tx["to"] = it.to?.let { StringType(it.toPrefixedHexString().value) } ?: StringType("")
                            tx["input"] = StringType(it.input.value)
                            tx["value"] = NumberType(it.value.value.toDouble()) // FIXME decimal value
                            tx["gas"] = NumberType(it.gas.value.toDouble()) // FIXME decimal value
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
            return ErrorType(t.message ?: "Unknown error") to null
        }
    }
}