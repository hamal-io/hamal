package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncInvokeRequest

class FuncInvokeFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {

            val correlationId = if (arg1.type("correlation_id") == KuaString::class) {
                CorrelationId(arg1.getString("correlation_id").stringValue)
            } else {
                CorrelationId.default
            }

            val version = if (arg1.type("version") == KuaNumber::class) {
                CodeVersion(arg1.getNumber("version").intValue)
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
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "exec_id" to KuaString(res.execId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}