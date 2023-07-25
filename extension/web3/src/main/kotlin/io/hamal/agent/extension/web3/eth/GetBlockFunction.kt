package io.hamal.agent.extension.web3.eth

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.web3.eth.abi.type.EthUint64
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

class GetBlockFunction : Function1In1Out<NumberValue, TableMapProxyValue>(
    FunctionInput1Schema(NumberValue::class),
    FunctionOutput1Schema(TableMapProxyValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: NumberValue): TableMapProxyValue {
        println("get block")
        val b = EthHttpBatchService(
            HttpTemplate("http://localhost:8081")
        ).getBlock(EthUint64(12345678)).execute().first()

        println(b)

        return ctx.tableCreateMap(0)
    }
}