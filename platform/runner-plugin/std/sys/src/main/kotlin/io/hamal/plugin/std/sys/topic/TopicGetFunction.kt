package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class TopicGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaMap?> {
        return try {
            null to sdk.topic.get(TopicId(arg1.value))
                .let { topic ->
                    KuaMap(
                        mutableMapOf(
                            "id" to KuaString(topic.id.value.value.toString(16)),
                            "name" to KuaString(topic.name.value),
                        )
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}