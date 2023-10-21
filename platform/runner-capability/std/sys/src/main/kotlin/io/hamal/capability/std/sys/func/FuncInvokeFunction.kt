package io.hamal.capability.std.sys.func

import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiInvokeFuncReq

class FuncInvokeFunction(
    private val sdk: ApiSdk
) : Function2In2Out<StringType, MapType, ErrorType, MapType>(
    FunctionInput2Schema(StringType::class, MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: MapType): Pair<ErrorType?, MapType?> {
        return try {

            val correlationId = if (arg2.type("correlation_id") == StringType::class) {
                CorrelationId(arg2.getString("correlation_id"))
            } else {
                CorrelationId.default
            }

            val res = sdk.func.invoke(
                FuncId(arg1.value),
                ApiInvokeFuncReq(
                    correlationId = correlationId,
                    inputs = InvocationInputs()
                )
            )

            null to MapType(
                mutableMapOf(
                    "req_id" to StringType(res.reqId.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "id" to StringType(res.id.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}