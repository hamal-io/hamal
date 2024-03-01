package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncService

class FuncListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(
                sdk.func.list(
                    ApiFuncService.FuncQuery(
                        namespaceIds = arg1.findArray("namespace_ids")
                            ?.asSequence()
                            ?.map { NamespaceId((it as KuaString).value) }
                            ?.toList()
                            ?: listOf(ctx[NamespaceId::class])
                    )
                ).map { func ->
                    ctx.tableCreate(
                        "id" to KuaString(func.id.value.value.toString(16)),
                        "namespace" to ctx.tableCreate(
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