package io.hamal.plugin.std.sys.hook

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiHookService.HookQuery

class HookListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaArray>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.hook.list(HookQuery(
                    namespaceIds = arg1.getArrayType("namespace_ids")
                        .map { NamespaceId((it.value as KuaString).value) }
                )).mapIndexed { index, hook ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(hook.id.value.value.toString(16)),
                            "namespace" to KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(hook.namespace.id.value.value.toString(16)),
                                    "name" to KuaString(hook.namespace.name.value)
                                )
                            ),
                            "name" to KuaString(hook.name.value),
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}