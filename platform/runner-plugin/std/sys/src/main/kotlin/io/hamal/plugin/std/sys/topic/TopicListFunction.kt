package io.hamal.plugin.std.sys.topic

import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTopicService

class TopicListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaArray>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.topic.list(
                    ApiTopicService.TopicQuery(

                    )
                ).mapIndexed { index, topic ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(topic.id.value.value.toString(16)),
                            "name" to KuaString(topic.name.value),
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}