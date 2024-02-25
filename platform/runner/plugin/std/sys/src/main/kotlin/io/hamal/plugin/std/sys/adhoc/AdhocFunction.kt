package io.hamal.plugin.std.sys.adhoc

import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxy
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiAdhocInvokeRequest

class AdhocFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable.Map, KuaError, KuaTable.Map>(
    FunctionInput1Schema(KuaTable.Map::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Map::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map): Pair<KuaError?, KuaTable.Map?> {
        return try {
            require(arg1 is TableProxy)


            val res = sdk.adhoc(
                ctx[NamespaceId::class],
//                arg1.findString("namespace_id")?.let { NamespaceId(SnowflakeId(it)) } ?: ctx[NamespaceId::class],
                ApiAdhocInvokeRequest(
                    inputs = InvocationInputs(),
                    code = CodeValue(arg1.getString("code"))
                )
            )

            null to KuaTable.Map(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "exec_id" to KuaString(res.execId.value.value.toString(16)),
                "workspace_id" to KuaString(res.workspaceId.value.value.toString(16)),
                "namespace_id" to KuaString(res.namespaceId.value.value.toString(16))

            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}