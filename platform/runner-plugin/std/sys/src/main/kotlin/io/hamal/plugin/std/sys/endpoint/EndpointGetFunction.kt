package io.hamal.plugin.std.sys.endpoint

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class EndpointGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.endpoint.get(EndpointId(arg1.value))
                .let { endpoint ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(endpoint.id.value.value.toString(16)),
                            "name" to StringType(endpoint.name.value),
                            "func" to MapType(
                                mutableMapOf(
                                    "id" to StringType(endpoint.func.id.value.value.toString(16)),
                                    "name" to StringType(endpoint.func.name.value)
                                )
                            ),
                            "method" to StringType(endpoint.method.name)
                        )
                    )
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}