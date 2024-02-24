package io.hamal.plugin.std.sys.exec

import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class ExecListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
//        return try {
//            val execs = sdk.exec.list(
//                Query(
//                    namespaceIds = arg1.getArrayType("namespace_ids").map { (_, string) ->
//                        NamespaceId(SnowflakeId(string.toString()))
//                    },
//                    workspaceIds = arg1.getArrayType("workspace_ids").map { (_, string) ->
//                        WorkspaceId(SnowflakeId(string.toString()))
//                    },
//                )
//            )
//            null to KuaArray(
//                execs.mapIndexed { index, exec ->
//                    index to KuaTable(
//                        mutableMapOf(
//                            "id" to KuaString(exec.id.value.value.toString(16)),
//                            "status" to KuaString(exec.status.toString()),
//                            "correlation_id" to (exec.correlation?.value?.let(::KuaString)
//                                ?: KuaNil)
//                        )
//                    )
//                }.toMap().toMutableMap()
//            )
//        } catch (t: Throwable) {
//            KuaError(t.message!!) to null
//        }
        TODO()
    }
}