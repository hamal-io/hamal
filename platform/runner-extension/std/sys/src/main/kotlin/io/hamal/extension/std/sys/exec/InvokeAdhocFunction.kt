package io.hamal.extension.std.sys.exec

import io.hamal.lib.domain.req.InvokeAdhocReq
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId

class InvokeAdhocFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        val r = InvokeAdhocReq(
            inputs = InvocationInputs(),
            code = CodeType(arg1.getString("code"))
        )

        val res = httpTemplate.post("/v1/adhoc")
            .body(r)
            .execute(ApiSubmittedReqWithId::class)

        return null to MapType().apply {
            this["req_id"] = res.reqId
            this["status"] = res.status.name
            this["id"] = res.id
        }
    }
}