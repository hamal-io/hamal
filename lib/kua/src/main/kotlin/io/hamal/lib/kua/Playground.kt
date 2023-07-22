package io.hamal.lib.kua

import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.value.Function2In0Out
import io.hamal.lib.kua.value.StringValue


fun main() {
    FixedPathLoader.load()

    Sandbox().use { sb ->
        val bridge = sb.bridge
    }
}


class TableTestFunc : Function2In0Out<TableMap, TableMap>(
    FunctionInput2Schema(TableMap::class, TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMap, arg2: TableMap) {

        arg2["cool"] = StringValue("up")

        println(arg1.set("test", StringValue("works")))
        println(arg1.set("test1", StringValue("works")))
        println(arg1.set("test2", StringValue("works")))
        println(arg1.set("test3", StringValue("works")))


    }

}