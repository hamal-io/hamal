package io.hamal.extension.std.sys.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HubSdk

class GetFuncFunction(
    private val sdk: HubSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        try {
            return null to sdk.func.get(FuncId(arg1.value))
                .let { func ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(func.id.value.value.toString(16)),
                            "namespace" to MapType(
                                mutableMapOf(
                                    "id" to StringType(func.namespace.id.value.value.toString(16)),
                                    "name" to StringType(func.namespace.name.value)
                                )
                            ),
                            "name" to StringType(func.name.value),
                            "code" to StringType(func.code.value)
                        )
                    )
                }
        } catch (t: Throwable) {
            return ErrorType(t.message!!) to null
        }
    }
}