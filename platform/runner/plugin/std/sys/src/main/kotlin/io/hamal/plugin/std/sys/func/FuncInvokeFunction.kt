package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncInvokeRequest

class FuncInvokeFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaMap>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaMap?> {
        return try {

            val correlationId = if (arg1.type("correlation_id") == KuaString::class) {
                CorrelationId(arg1.getString("correlation_id"))
            } else {
                CorrelationId.default
            }

            val version = if (arg1.type("version") == KuaNumber::class) {
                CodeVersion(arg1.getInt("version"))
            } else {
                null
            }

            val res = sdk.func.invoke(
                FuncId(SnowflakeId(arg1.getString("id"))),
                ApiFuncInvokeRequest(
                    correlationId = correlationId,
                    inputs = InvocationInputs(),
                    version = version
                )
            )

            null to KuaMap(
                mutableMapOf(
                    "id" to KuaString(res.id.value.value.toString(16)),
                    "status" to KuaString(res.status.name),
                    "exec_id" to KuaString(res.execId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}