package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExecService
import io.hamal.lib.value.ValueError
import io.hamal.lib.value.ValueNil
import io.hamal.lib.value.ValueString

class ExecListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val execs = sdk.exec.list(
                ApiExecService.ExecQuery(
                    namespaceIds = arg1.findTable("namespace_ids")
                        ?.asList()
                        ?.map { NamespaceId((it as ValueString).stringValue) }
                        ?.toList()
                        ?: listOf(ctx[NamespaceId::class]),
                    workspaceIds = arg1.findTable("workspace_ids")
                        ?.asList()
                        ?.map { WorkspaceId((it as ValueString).stringValue) }
                        ?.toList()
                        ?: listOf(ctx[WorkspaceId::class])
                )
            )
            null to ctx.tableCreate(
                execs.map { exec ->
                    ctx.tableCreate(
                        "id" to ValueString(exec.id.value.value.toString(16)),
                        "status" to ValueString(exec.status.toString()),
                        "correlation_id" to (exec.correlation?.value?.let(::ValueString) ?: ValueNil)
                    )
                }
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}