package io.hamal.plugin.std.sys.hook

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.HookName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.createTable
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiHookCreateRequest

class HookCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.hook.create(
                arg1.findString("namespace_id")?.let { NamespaceId(SnowflakeId(it)) } ?: ctx[NamespaceId::class],
                ApiHookCreateRequest(
                    name = HookName(arg1.getString("name"))
                )
            )

            null to ctx.createTable(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "hook_id" to KuaString(res.hookId.value.value.toString(16)),
                "workspace_id" to KuaString(res.workspaceId.value.value.toString(16)),
                "namespace_id" to KuaString(res.namespaceId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}