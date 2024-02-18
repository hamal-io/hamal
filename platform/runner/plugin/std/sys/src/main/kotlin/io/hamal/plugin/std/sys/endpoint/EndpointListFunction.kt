package io.hamal.plugin.std.sys.endpoint

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
import io.hamal.lib.sdk.api.ApiEndpointService.EndpointQuery

class EndpointListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaArray>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.endpoint.list(EndpointQuery(
                    namespaceIds = arg1.getArrayType("namespace_ids").map { NamespaceId((it.value as KuaString).value) }
                )).mapIndexed { index, endpoint ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(endpoint.id.value.value.toString(16)),
                            "func" to KuaMap(
                                mutableMapOf(
                                    "id" to KuaString(endpoint.func.id.value.value.toString(16)),
                                    "name" to KuaString(endpoint.func.name.value)
                                )
                            ),
                            "name" to KuaString(endpoint.name.value)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}