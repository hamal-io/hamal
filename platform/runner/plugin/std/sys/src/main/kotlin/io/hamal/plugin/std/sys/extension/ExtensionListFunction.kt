package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class ExtensionListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaTable.Array>(
    FunctionOutput2Schema(KuaError::class, KuaTable.Array::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable.Array?> {
        return try {
            val extensions = sdk.extension.list(ctx[WorkspaceId::class])
            null to KuaTable.Array(
                extensions.mapIndexed { index, ext ->
                    index to KuaTable.Map(
                        mutableMapOf(
                            "id" to KuaString(ext.id.value.value.toString(16)),
                            "name" to KuaString(ext.name.value)
                        )
                    )
                }.toMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}