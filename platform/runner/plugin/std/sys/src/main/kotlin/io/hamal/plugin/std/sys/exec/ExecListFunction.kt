package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toArray
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExecService

class ExecListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTableMap, KuaError, KuaTableArray>(
    FunctionInput1Schema(KuaTableMap::class),
    FunctionOutput2Schema(KuaError::class, KuaTableArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap): Pair<KuaError?, KuaTableArray?> {
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
            null to ctx.toArray(
                execs.map { exec ->
                    ctx.toMap(
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