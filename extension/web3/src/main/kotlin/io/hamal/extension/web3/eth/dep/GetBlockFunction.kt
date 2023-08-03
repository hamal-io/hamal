package io.hamal.extension.web3.eth.dep

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService

class GetBlockFunction(
    val config: ExtensionConfig
) : Function1In2Out<NumberValue, ErrorValue, TableMapValue>(
    FunctionInput1Schema(NumberValue::class),
    FunctionOutput2Schema(ErrorValue::class, TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: NumberValue): Pair<ErrorValue?, TableMapValue?> {
        println("get block from - ${config.value["host"]}")

        val b = EthHttpBatchService(
            HttpTemplate((config.value["host"] as StringValue).value)
        ).getBlock(EthUint64(arg1.value.toLong())).execute().first() as EthGetBlockResp

        return null to ctx.tableCreateMap(0).also { table ->
            table["id"] = b.result.number.value.toLong()
            table["hash"] = b.result.hash.toPrefixedHexString().value
            table["parent_hash"] = b.result.parentHash.toPrefixedHexString().value
            table["transaction_count"] = b.result.transactions.size
            table["gas_used"] = b.result.gasUsed.value.toLong()
            table["gas_limit"] = b.result.gasLimit.value.toLong()
        }
    }
}