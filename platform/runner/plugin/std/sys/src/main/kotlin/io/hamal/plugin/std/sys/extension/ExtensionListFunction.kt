package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class ExtensionListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaArray>(
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaArray?> {
        return try {
            val extensions = sdk.extension.list(ctx[WorkspaceId::class])
            null to KuaArray(
                extensions.mapIndexed { index, ext ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(ext.id.value.value.toString(16)),
                            "name" to KuaString(ext.name.value)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}