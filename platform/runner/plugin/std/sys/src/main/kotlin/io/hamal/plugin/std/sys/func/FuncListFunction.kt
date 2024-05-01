package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncService

class FuncListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(
                sdk.func.list(
                    ApiFuncService.FuncQuery(
                        namespaceIds = arg1.findTable("namespace_ids")
                            ?.asList()
                            ?.map { NamespaceId((it as ValueString).stringValue) }
                            ?.toList()
                            ?: listOf(ctx[NamespaceId::class])
                    )
                ).map { func ->
                    ctx.tableCreate(
                        "id" to ValueString(func.id.stringValue),
                        "namespace" to ctx.tableCreate(
                            "id" to ValueString(func.namespace.id.stringValue),
                            "name" to func.namespace.name
                        ),
                        "name" to func.name.value,
                    )
                }
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}