package io.hamal.lib.kua

import io.hamal.lib.kua.function.Function2In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue


fun main() {
    FixedPathLoader.load()

    Sandbox().use { sb ->
        val bridge = sb.bridge
    }
}


class TableTestFunc : Function2In0Out<TableMapValue, TableMapValue>(
    FunctionInput2Schema(TableMapValue::class, TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue, arg2: TableMapValue) {

        arg2["cool"] = StringValue("up")

        println(arg1.set("test", StringValue("works")))
        println(arg1.set("test1", StringValue("works")))
        println(arg1.set("test2", StringValue("works")))
        println(arg1.set("test3", StringValue("works")))


    }

}