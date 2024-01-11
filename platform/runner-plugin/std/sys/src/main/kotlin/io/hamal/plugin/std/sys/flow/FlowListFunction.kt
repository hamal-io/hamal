package io.hamal.plugin.std.sys.flow

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class FlowListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaArray>(
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.flow.list(ctx[GroupId::class]).mapIndexed { index, flow ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(flow.id.value.value.toString(16)),
                            "name" to KuaString(flow.name.value),
                            "type" to KuaString(flow.type.value)
                        )
                    )
                }.toMap().toMutableMap()
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}