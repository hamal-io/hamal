package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableMapValue


fun main() {
    NativeLoader.load(BuildDir)

    Sandbox().also {
        it.register(Extension("test", listOf(NamedFunctionValue("iterator", TableMapTestFunction()))))
    }.use { sb ->
        sb.load(
            """
            local table = {
                1,
                2,
                3
            }
            test.iterator(table)
            """.trimIndent()
        )

    }
}

class TableMapTestFunction : Function1In0Out<TableMapValue>(
    FunctionInput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue) {
        val it = TableEntryIterator(
            ctx.absIndex(ctx.absIndex(-1)),
            ctx,
            keyExtractor = { state, index -> state.getNumberValue(index) },
            valueExtractor = { state, index -> state.getAnyValue(index) }
        )
        ctx.pushNil()
        it.asSequence().forEach { entry ->
            println(entry)
        }
    }
}