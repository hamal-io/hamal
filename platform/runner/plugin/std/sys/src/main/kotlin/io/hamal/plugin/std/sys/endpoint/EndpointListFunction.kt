package io.hamal.plugin.std.sys.endpoint

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiEndpointService

class EndpointListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable.Map, KuaError, KuaTable.Array>(
    FunctionInput1Schema(KuaTable.Map::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Array::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map): Pair<KuaError?, KuaTable.Array?> {
        return try {
            null to KuaTable.Array(
                sdk.endpoint.list(ApiEndpointService.EndpointQuery(
                    namespaceIds = arg1.findArray("namespace_ids")
                        ?.asSequence()
                        ?.map { NamespaceId((it as KuaString).value) }
                        ?.toList()
                        ?: listOf(ctx[NamespaceId::class])

                )).mapIndexed { index, endpoint ->
                    index to KuaTable.Map(
                        "id" to KuaString(endpoint.id.value.value.toString(16)),
                        "func" to KuaTable.Map(
                            "id" to KuaString(endpoint.func.id.value.value.toString(16)),
                            "name" to KuaString(endpoint.func.name.value)
                        ),
                        "name" to KuaString(endpoint.name.value)
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}