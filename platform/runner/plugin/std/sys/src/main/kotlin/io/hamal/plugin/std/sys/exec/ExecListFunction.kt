package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExecService

class ExecListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val execs = sdk.exec.list(
                ApiExecService.ExecQuery(
                    namespaceIds = arg1.findTable("namespace_ids")
                        ?.asList()
                        ?.map { NamespaceId((it as KuaString).stringValue) }
                        ?.toList()
                        ?: listOf(ctx[NamespaceId::class]),
                    workspaceIds = arg1.findTable("workspace_ids")
                        ?.asList()
                        ?.map { WorkspaceId((it as KuaString).stringValue) }
                        ?.toList()
                        ?: listOf(ctx[WorkspaceId::class])
                )
            )
            null to ctx.tableCreate(
                execs.map { exec ->
                    ctx.tableCreate(
                        "id" to KuaString(exec.id.value.value.toString(16)),
                        "status" to KuaString(exec.status.toString()),
                        "correlation_id" to (exec.correlation?.value?.let(::KuaString) ?: KuaNil)
                    )
                }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}