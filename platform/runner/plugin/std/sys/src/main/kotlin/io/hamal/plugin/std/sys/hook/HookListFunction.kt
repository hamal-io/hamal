package io.hamal.plugin.std.sys.hook

import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class HookListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
//        return try {
//            null to KuaArray(
//                sdk.hook.list(HookQuery(
//                    namespaceIds = arg1.getArrayType("namespace_ids")
//                        .map { NamespaceId((it.value as KuaString).value) }
//                )).mapIndexed { index, hook ->
//                    index to KuaTable(
//                        mutableMapOf(
//                            "id" to KuaString(hook.id.value.value.toString(16)),
//                            "namespace" to KuaTable(
//                                mutableMapOf(
//                                    "id" to KuaString(hook.namespace.id.value.value.toString(16)),
//                                    "name" to KuaString(hook.namespace.name.value)
//                                )
//                            ),
//                            "name" to KuaString(hook.name.value),
//                        )
//                    )
//                }.toMap().toMutableMap()
//            )
//        } catch (t: Throwable) {
//            KuaError(t.message!!) to null
//        }

        TODO()
    }
}