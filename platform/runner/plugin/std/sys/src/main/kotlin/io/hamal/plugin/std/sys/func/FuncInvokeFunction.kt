package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getNumber
import io.hamal.lib.kua.value.getString
import io.hamal.lib.kua.value.type
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncInvokeRequest

class FuncInvokeFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {

            val correlationId = if (arg1.type("correlation_id") == ValueString::class) {
                CorrelationId(arg1.getString("correlation_id"))
            } else {
                CorrelationId.default
            }

            val version = if (arg1.type("version") == ValueNumber::class) {
                CodeVersion(arg1.getNumber("version"))
            } else {
                null
            }

            val res = sdk.func.invoke(
                FuncId(SnowflakeId(arg1.getString("id").stringValue)),
                ApiFuncInvokeRequest(
                    correlationId = correlationId,
                    inputs = InvocationInputs(),
                    version = version
                )
            )

            null to ctx.tableCreate(
                "request_id" to res.requestId,
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue)
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}