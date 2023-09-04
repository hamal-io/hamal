package io.hamal.extension.std.sys.exec

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.HubExecList

class ListExecFunction(
    private val httpTemplate: HttpTemplate
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        val execs = try {
            httpTemplate.get("/v1/execs")
                .execute(HubExecList::class)
                .execs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf()
        }

        return null to ArrayType(
            execs.mapIndexed { index, exec ->
                index to MapType(
                    mutableMapOf(
                        "id" to StringType(exec.id.value.value.toString(16)),
                        "status" to StringType(exec.status.toString())
                    )
                )
            }.toMap().toMutableMap()
        )

    }
}