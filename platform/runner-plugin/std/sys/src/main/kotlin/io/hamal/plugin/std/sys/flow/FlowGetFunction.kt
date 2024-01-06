package io.hamal.plugin.std.sys.flow

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class FlowGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaMap?> {
        return try {
            null to sdk.flow.get(FlowId(arg1.value))
                .let { flow ->
                    KuaMap(
                        mutableMapOf(
                            "id" to KuaString(flow.id.value.value.toString(16)),
                            "name" to KuaString(flow.name.value),
                            "type" to KuaString(flow.type.value)
                        )
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}