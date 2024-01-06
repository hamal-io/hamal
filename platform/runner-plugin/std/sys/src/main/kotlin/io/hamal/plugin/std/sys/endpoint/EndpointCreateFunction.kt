package io.hamal.plugin.std.sys.endpoint

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.EndpointName
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiEndpointCreateRequest

class EndpointCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaMap>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaMap?> {
        return try {
            val res = sdk.endpoint.create(
                arg1.findString("flow_id")?.let { FlowId(SnowflakeId(it)) } ?: ctx[FlowId::class],
                ApiEndpointCreateRequest(
                    funcId = FuncId(arg1.getString("func_id")),
                    name = EndpointName(arg1.getString("name")),
                    method = EndpointMethod.valueOf(arg1.getString("method"))
                )
            )

            null to KuaMap(
                mutableMapOf(
                    "id" to KuaString(res.id.value.value.toString(16)),
                    "status" to KuaString(res.status.name),
                    "endpoint_id" to KuaString(res.endpointId.value.value.toString(16)),
                    "group_id" to KuaString(res.groupId.value.value.toString(16)),
                    "func_id" to KuaString(res.funcId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}