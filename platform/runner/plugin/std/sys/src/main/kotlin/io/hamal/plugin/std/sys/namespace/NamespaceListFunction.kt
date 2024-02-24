package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class NamespaceListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaTable>(
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
//        return try {
//            null to sdk.namespace.list(ctx[WorkspaceId::class]).toKua()
//        } catch (t: Throwable) {
//            KuaError(t.message!!) to null
//        }
//    }
//
//    private fun List<ApiNamespaceList.Namespace>.toKua(): KuaArray {
//        return KuaArray(
//            mapIndexed { index, namespace ->
//                index to KuaTable(
//                    mutableMapOf(
//                        "id" to KuaString(namespace.id.value.value.toString(16)),
//                        "parent_id" to KuaString(namespace.parentId.value.value.toString(16)),
//                        "name" to KuaString(namespace.name.value),
//                    )
//                )
//            }.toMap().toMutableMap()
//        )
        TODO()
    }
}