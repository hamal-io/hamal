package io.hamal.plugin.std.sys.exec

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExecService.Query

class ExecListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaArray>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaArray?> {
        return try {
            val execs = sdk.exec.list(
                Query(
                    namespaceIds = arg1.getArrayType("namespace_ids").map { (_, string) ->
                        NamespaceId(SnowflakeId(string.toString()))
                    },
                    workspaceIds = arg1.getArrayType("workspace_ids").map { (_, string) ->
                        WorkspaceId(SnowflakeId(string.toString()))
                    },
                )
            )
            null to KuaArray(
                execs.mapIndexed { index, exec ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(exec.id.value.value.toString(16)),
                            "status" to KuaString(exec.status.toString()),
                            "correlation_id" to (exec.correlation?.correlationId?.value?.let(::KuaString)
                                ?: KuaNil)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}