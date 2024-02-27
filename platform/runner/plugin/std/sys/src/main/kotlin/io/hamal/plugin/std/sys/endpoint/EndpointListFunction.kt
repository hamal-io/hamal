package io.hamal.plugin.std.sys.endpoint

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiEndpointService

class EndpointListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(
                sdk.endpoint.list(ApiEndpointService.EndpointQuery(
                    namespaceIds = arg1.findArray("namespace_ids")
                        ?.asSequence()
                        ?.map { NamespaceId((it as KuaString).value) }
                        ?.toList()
                        ?: listOf(ctx[NamespaceId::class])

                )).map { endpoint ->
                    ctx.tableCreate(
                        "id" to KuaString(endpoint.id.value.value.toString(16)),
                        "func" to ctx.tableCreate(
                            "id" to KuaString(endpoint.func.id.value.value.toString(16)),
                            "name" to KuaString(endpoint.func.name.value)
                        ),
                        "name" to KuaString(endpoint.name.value)
                    )
                }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}