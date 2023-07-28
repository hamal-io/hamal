package io.hamal.agent.extension.web3.eth

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService

//class GetBlockFunction : Function1In2Out<NumberValue, TableMapProxyValue, ErrorValue>(
//    FunctionInput1Schema(NumberValue::class),
//    FunctionOutput2Schema(TableMapProxyValue::class, ErrorValue::class)
//) {
//    override fun invoke(ctx: FunctionContext, arg1: NumberValue): Pair<TableMapProxyValue, ErrorValue> {
//        println("get block")
//        return Pair(ctx.tableCreateMap(0), ErrorValue("some error message"))
//    }
//}

class GetBlockFunction : Function1In2Out<NumberValue, ErrorValue, TableMapProxyValue>(
    FunctionInput1Schema(NumberValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableMapProxyValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: NumberValue): Pair<ErrorValue?, TableMapProxyValue?> {
        println("get block")
        val b = EthHttpBatchService(
            HttpTemplate("http://localhost:8081")
        ).getBlock(EthUint64(arg1.value.toLong())).execute().first() as EthGetBlockResp

//        println(b)

        return null to ctx.tableCreateMap(0).also { table ->
            table["id"] = b.result.number.value.toLong()
            table["hash"] = b.result.hash.toPrefixedHexString().value
            table["parent_hash"] = b.result.parentHash.toPrefixedHexString().value
            table["transaction_count"] = b.result.transactions.size
            table["gas_used"] = b.result.gasUsed.value.toLong()
            table["gas_limit"] = b.result.gasLimit.value.toLong()
        }
//        return null to null
    }
}