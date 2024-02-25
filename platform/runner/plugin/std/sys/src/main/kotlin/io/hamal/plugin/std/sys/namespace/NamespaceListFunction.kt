package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiNamespaceList

class NamespaceListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaTable.Array>(
    FunctionOutput2Schema(KuaError::class, KuaTable.Array::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable.Array?> {
        return try {
            null to sdk.namespace.list(ctx[WorkspaceId::class]).toKua()
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }

    private fun List<ApiNamespaceList.Namespace>.toKua(): KuaTable.Array {
        return KuaTable.Array(
            mapIndexed { index, namespace ->
                index to KuaTable.Map(
                    "id" to KuaString(namespace.id.value.value.toString(16)),
                    "parent_id" to KuaString(namespace.parentId.value.value.toString(16)),
                    "name" to KuaString(namespace.name.value),
                )
            }.toMap().toMutableMap()
        )
    }
}