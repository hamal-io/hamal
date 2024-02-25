package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaNil
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExecService

class ExecListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable.Map, KuaError, KuaTable.Array>(
    FunctionInput1Schema(KuaTable.Map::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Array::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map): Pair<KuaError?, KuaTable.Array?> {
        return try {
            val execs = sdk.exec.list(
                ApiExecService.ExecQuery(
                    namespaceIds = arg1.findArray("namespace_ids")
                        ?.asSequence()
                        ?.map { NamespaceId((it as KuaString).value) }
                        ?.toList()
                        ?: listOf(ctx[NamespaceId::class]),
                    workspaceIds = arg1.findArray("workspace_ids")
                        ?.asSequence()
                        ?.map { WorkspaceId((it as KuaString).value) }
                        ?.toList()
                        ?: listOf(ctx[WorkspaceId::class])
                )
            )
            null to KuaTable.Array(
                execs.mapIndexed { index, exec ->
                    index to KuaTable.Map(
                        "id" to KuaString(exec.id.value.value.toString(16)),
                        "status" to KuaString(exec.status.toString()),
                        "correlation_id" to (exec.correlation?.value?.let(::KuaString) ?: KuaNil)
                    )
                }.toMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}