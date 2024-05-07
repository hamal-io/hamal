package io.hamal.plugin.std.sys.extension

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class ExtensionListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<ValueError, KuaTable>(
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ValueError?, KuaTable?> {
        return try {
            val extensions = sdk.extension.list(ctx[WorkspaceId::class])
            null to ctx.tableCreate(
                extensions.map { ext ->
                    ctx.tableCreate(
                        "id" to ValueString(ext.id.stringValue),
                        "name" to ext.name
                    )
                }
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}