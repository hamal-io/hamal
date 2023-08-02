package io.hamal.extension.web3.eth

import io.hamal.lib.kua.extension.Extension
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.extension.ExtensionFactory
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.NumberValue

class EthRequestExtensionFactory : ExtensionFactory {
    override fun create(): Extension {
        return Extension(
            name = "request",
            config = ExtensionConfig(),
            functions = listOf(
                NamedFunctionValue("get_block", EthRequestGetBlockFunction)
            ),
            extensions = listOf()
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