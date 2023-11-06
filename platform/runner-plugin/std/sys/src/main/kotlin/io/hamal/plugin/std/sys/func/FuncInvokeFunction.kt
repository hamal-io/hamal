package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncInvokeReq

class FuncInvokeFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {

            val correlationId = if (arg1.type("correlation_id") == StringType::class) {
                CorrelationId(arg1.getString("correlation_id"))
            } else {
                CorrelationId.default
            }

            val res = sdk.func.invoke(
                FuncId(SnowflakeId(arg1.getString("id"))),
                ApiFuncInvokeReq(
                    correlationId = correlationId,
                    inputs = InvocationInputs(),
                    events = listOf()
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "exec_id" to StringType(res.execId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}