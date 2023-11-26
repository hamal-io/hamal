package io.hamal.plugin.std.sys.hook

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiHookService.HookQuery

class HookListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, ArrayType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.hook.list(HookQuery(
                    flowIds = arg1.getArrayType("flow_ids")
                        .map { FlowId((it.value as StringType).value) }
                )).mapIndexed { index, hook ->
                    index to MapType(
                        mutableMapOf(
                            "id" to StringType(hook.id.value.value.toString(16)),
                            "flow" to MapType(
                                mutableMapOf(
                                    "id" to StringType(hook.flow.id.value.value.toString(16)),
                                    "name" to StringType(hook.flow.name.value)
                                )
                            ),
                            "name" to StringType(hook.name.value),
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}