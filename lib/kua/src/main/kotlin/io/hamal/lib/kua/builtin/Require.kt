package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.Registry
import io.hamal.lib.kua.function.Function1In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.StringValue
import java.nio.file.Files
import kotlin.io.path.Path

class Require(
    val registry: Registry
) : Function1In1Out<StringValue, TableMapValue>(
    FunctionInput1Schema(StringValue::class),
    FunctionOutput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue): TableMapValue {
        println("importing module ${arg1}")

        ctx.load(String(Files.readAllBytes(Path("/home/ddymke/Repo/hamal/lib/kua/src/main/resources/extension.lua"))))
        ctx.load(
            """
                                    instance = ethFactory()
                                """.trimIndent()
        )

        return ctx.getGlobalTableMap("instance")

//                                val result = ctx.tableCreateMap(1)
//
//                                return result
    }
}
