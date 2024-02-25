package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toArray
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTableArray
import io.hamal.lib.sdk.ApiSdk

class NamespaceListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaTableArray>(
    FunctionOutput2Schema(KuaError::class, KuaTableArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTableArray?> {
        return try {
            null to ctx.toArray(sdk.namespace.list(ctx[WorkspaceId::class]).map { namespace ->
                ctx.toMap(
                    "id" to KuaString(namespace.id.value.value.toString(16)),
                    "parent_id" to KuaString(namespace.parentId.value.value.toString(16)),
                    "name" to KuaString(namespace.name.value),
                )
            })
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}