package io.hamal.extension.web3.eth.dep

import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.extension.NativeExtensionFactory
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.NumberValue

class EthRequestExtensionFactory : NativeExtensionFactory {
    override fun create(): NativeExtension {
        return NativeExtension(
            name = "request",
            config = ExtensionConfig(),
            values = mapOf()
        )
    }
}

object EthRequestGetBlockFunction : Function1In1Out<NumberValue, TableMapValue>(
    FunctionInput1Schema(NumberValue::class),
    FunctionOutput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: NumberValue): TableMapValue {
        val result = ctx.tableCreateMap(2)
        result["req_type"] = "get_block"
        result["block"] = arg1
        return result
    }
}