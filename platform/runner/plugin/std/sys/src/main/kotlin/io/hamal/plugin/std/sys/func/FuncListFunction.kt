package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toArray
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncService

class FuncListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable.Map, KuaError, KuaTable.Array>(
    FunctionInput1Schema(KuaTable.Map::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Array::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map): Pair<KuaError?, KuaTable.Array?> {
        return try {
            null to ctx.toArray(
                sdk.func.list(
                    ApiFuncService.FuncQuery(
                        namespaceIds = arg1.findArray("namespace_ids")
                            ?.asSequence()
                            ?.map { NamespaceId((it as KuaString).value) }
                            ?.toList()
                            ?: listOf(ctx[NamespaceId::class])
                    )
                ).map { func ->
                    ctx.toMap(
                        "id" to KuaString(func.id.value.value.toString(16)),
                        "namespace" to ctx.toMap(
                            "id" to KuaString(func.namespace.id.value.value.toString(16)),
                            "name" to KuaString(func.namespace.name.value)
                        ),
                        "name" to KuaString(func.name.value),
                    )
                }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}