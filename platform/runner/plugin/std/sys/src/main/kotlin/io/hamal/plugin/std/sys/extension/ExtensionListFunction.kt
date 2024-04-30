package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueString

class ExtensionListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaTable>(
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
        return try {
            val extensions = sdk.extension.list(ctx[WorkspaceId::class])
            null to ctx.tableCreate(
                extensions.map { ext ->
                    ctx.tableCreate(
                        "id" to ValueString(ext.id.value.value.toString(16)),
                        "name" to ValueString(ext.name.value)
                    )
                }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}