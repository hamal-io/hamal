package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class NamespaceListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaArray>(
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.namespace.list(ctx[GroupId::class]).mapIndexed { index, namespace ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(namespace.id.value.value.toString(16)),
                            "name" to KuaString(namespace.name.value),
                            "type" to KuaString(namespace.type.value)
                        )
                    )
                }.toMap().toMutableMap()
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}