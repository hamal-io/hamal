package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.toHotObject
import io.hamal.lib.sdk.ApiSdk

class TopicEntryAppendFunction(
    private val sdk: ApiSdk
) : Function2In2Out<KuaString, KuaMap, KuaError, KuaMap>(
    FunctionInput2Schema(KuaString::class, KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaString, arg2: KuaMap): Pair<KuaError?, KuaMap?> {
        return try {
            val res = sdk.topic.append(
                TopicId(arg1.value),
                TopicEntryPayload(arg2.toHotObject())
            )

            null to KuaMap(
                mutableMapOf(
                    "id" to KuaString(res.id.value.value.toString(16)),
                    "status" to KuaString(res.status.name),
                    "topic_id" to KuaString(res.topicId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}