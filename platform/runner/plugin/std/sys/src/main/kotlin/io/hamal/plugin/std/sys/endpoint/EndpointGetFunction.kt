package io.hamal.plugin.std.sys.endpoint

import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class EndpointGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.endpoint.get(EndpointId(arg1.value))
                .let { endpoint ->
                    KuaTable(
                        mutableMapOf(
                            "id" to KuaString(endpoint.id.value.value.toString(16)),
                            "name" to KuaString(endpoint.name.value),
                            "func" to KuaTable(
                                mutableMapOf(
                                    "id" to KuaString(endpoint.func.id.value.value.toString(16)),
                                    "name" to KuaString(endpoint.func.name.value)
                                )
                            )
                        )
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}