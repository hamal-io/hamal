package io.hamal.plugin.std.sys.request

import io.hamal.lib.http.HttpErrorResponse
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.createTable
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.api.ApiRequested

class RequestGetFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        val response = httpTemplate.get("/v1/requests/{reqId}")
            .path("reqId", arg1.value)
            .execute()

        if (response is HttpSuccessResponse) {
            return null to response.result(ApiRequested::class)
                .let { exec ->
                    ctx.createTable(
                        "reqId" to KuaString(exec.id.value.value.toString(16)),
                        "status" to KuaString(exec.status.name)
                    )
                }
        } else {
            require(response is HttpErrorResponse)
//            return response.error(HamalError::class)
//                .let { error ->
//                    ErrorValue(error.message ?: "An unknown error occurred")
//                }

            TODO()
        }
//
    }
}