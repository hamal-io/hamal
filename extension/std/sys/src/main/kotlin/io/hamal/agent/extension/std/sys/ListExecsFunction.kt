package io.hamal.agent.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.value.Function0In1Out
import io.hamal.lib.sdk.domain.ListExecsResponse

class ListExecsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In1Out<TableMap>(
    FunctionOutput1Schema(TableMap::class)
) {
    //    override fun invoke(ctx: FuncContext): Value {
//        println("ListExecs")
//
//        val response = templateSupplier()
//            .get("/v1/execs")
//            .execute(ListExecsResponse::class)
//            .execs
//            .mapIndexed { idx, exec ->
//                IdentValue((idx + 1).toString()) to TableValue(
//                    "id" to StringValue(exec.id.value.toString()),
//                    "status" to StringValue(exec.status.name)
//                )
//            }.toMap<IdentValue, Value>()
//
//        return TableValue(response)
//    }
    override fun invoke(ctx: FunctionContext): TableMap {
        println("ListExecs")

        val execs = templateSupplier()
            .get("/v1/execs")
            .execute(ListExecsResponse::class)
            .execs

        println(execs)

//            .mapIndexed { idx, exec ->
//                IdentValue((idx + 1).toString()) to TableValue(
//                    "id" to StringValue(exec.id.value.toString()),
//                    "status" to StringValue(exec.status.name)
//                )
//            }.toMap<IdentValue, Value>()


        return ctx.createMapTable(1).also {
            it.set("test", "xyz")
            it["answer"] = 42
        }
    }
}