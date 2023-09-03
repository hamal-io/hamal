package io.hamal.extension.std.sys.func

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.domain.ApiSubmittedReqWithId

class CreateFuncFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType> {
        try {
            val namespaceId = if (arg1.type("namespace_id") == StringType::class) {
                NamespaceId(SnowflakeId(arg1.getString("namespace_id")))
            } else {
                null
            }

            val r = CreateFuncReq(
                namespaceId = namespaceId,
                name = FuncName(arg1.getString("name")),
                inputs = FuncInputs(),
                code = arg1.getCodeType("code")
            )

            val res = httpTemplate.post("/v1/funcs")
                .body(r)
                .execute(ApiSubmittedReqWithId::class)

            return null to MapType(
                mutableMapOf(
                    "req_id" to StringType(res.reqId.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "id" to StringType(res.id.value.toString(16))
                )
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}