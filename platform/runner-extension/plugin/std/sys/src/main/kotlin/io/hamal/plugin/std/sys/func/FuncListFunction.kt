package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncService.FuncQuery

class FuncListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, ArrayType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.func.list(
                    FuncQuery(
                        namespaceIds = arg1.getArrayType("namespace_ids")
                            .map { NamespaceId((it.value as StringType).value) }
                    )
                ).mapIndexed { index, func ->
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
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}