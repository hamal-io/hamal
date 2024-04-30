package io.hamal.plugin.std.sys.adhoc

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.findString
import io.hamal.lib.kua.type.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest
import io.hamal.lib.value.ValueError
import io.hamal.lib.value.ValueString

class AdhocFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {

            val res = sdk.adhoc(
                namespaceId = arg1.findString("namespace_id")
                    ?.let { NamespaceId(SnowflakeId(it.stringValue)) }
                    ?: ctx[NamespaceId::class],
                request = ApiAdhocInvokeRequest(
                    inputs = InvocationInputs(),
                    code = CodeValue(arg1.getString("code").stringValue)
                )
            )
            return null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.value.value.toString(16)),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.value.value.toString(16)),
                "workspace_id" to ValueString(res.workspaceId.value.value.toString(16)),
                "namespace_id" to ValueString(res.namespaceId.value.value.toString(16))
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            ValueError(t.message!!) to null
        }
    }
}