package io.hamal.plugin.std.sys.hook

import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class HookGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.hook.get(HookId(arg1.value))
                .let { hook ->
                    MapType(
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
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}