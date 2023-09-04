package io.hamal.extension.std.sys.func

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.ApiFuncList

class ListFuncFunction(
    private val httpTemplate: HttpTemplate
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        val funcs = try {
            httpTemplate.get("/v1/funcs")
                .execute(ApiFuncList::class)
                .funcs
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf<ApiFuncList.ApiSimpleFunc>()
        }

        return null to ArrayType(
            funcs.mapIndexed { index, func ->
                index to MapType(
                    mutableMapOf(
                        "id" to StringType(func.id.value.value.toString(16)),
                        "namespace" to MapType(
                            mutableMapOf(
                                "id" to StringType(func.namespace.id.value.value.toString(16)),
                                "name" to StringType(func.namespace.name.value)
                            )
                        ),
                        "name" to StringType(func.name.value),
                    )
                )
            }.toMap().toMutableMap()
        )
    }
}