package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.createTable
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class NamespaceListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaTable>(
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.createTable(sdk.namespace.list(ctx[WorkspaceId::class]).map { namespace ->
                ctx.createTable(
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