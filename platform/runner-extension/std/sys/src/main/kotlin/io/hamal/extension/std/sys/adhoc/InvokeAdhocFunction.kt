package io.hamal.extension.std.sys.adhoc

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.sdk.HubSdk

class InvokeAdhocFunction(
    private val sdk: HubSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.adhoc(
                ctx[GroupId::class],
                InvokeAdhocReq(
                    inputs = InvocationInputs(),
                    code = CodeType(arg1.getString("code"))
                )
            )

            null to MapType().apply {
                this["req_id"] = res.reqId
                this["status"] = res.status.name
                this["id"] = res.id
            }

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}