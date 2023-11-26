package io.hamal.plugin.std.sys.endpoint

import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiEndpointService.EndpointQuery

class EndpointListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, ArrayType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.endpoint.list(EndpointQuery()).mapIndexed { index, endpoint ->
                    index to MapType(
                        mutableMapOf(
                            "id" to StringType(endpoint.id.value.value.toString(16)),
                            "func" to MapType(
                                mutableMapOf(
                                    "id" to StringType(endpoint.func.id.value.value.toString(16)),
                                    "name" to StringType(endpoint.func.name.value)
                                )
                            ),
                            "name" to StringType(endpoint.name.value)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}