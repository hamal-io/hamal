package io.hamal.agent.extension.std.sys

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.value.Function0In1Out
import io.hamal.lib.sdk.domain.ListExecsResponse

class ListExecsFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function0In1Out<TableArray>(
    FunctionOutput1Schema(TableArray::class)
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
    override fun invoke(ctx: FunctionContext): TableArray {
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


        return ctx.createArrayTable(1).also {
            execs.forEach { exec ->
                val inner = ctx.createMapTable(2)
                inner["id"] = exec.id.value.value.toString()
                inner["status"] = exec.status.toString()
//                it.append(exec.id.value.value.toString())
//                it.append(exec.status.toString())
                ctx.state.pushTop(inner.index)
                ctx.state.tableInsert(1)
            }
        }
    }
}