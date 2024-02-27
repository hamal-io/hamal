package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class TopicEntryAppendFunction(
    private val sdk: ApiSdk
) : Function2In2Out<KuaString, KuaTable, KuaError, KuaTable>(
    FunctionInput2Schema(KuaString::class, KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaString, arg2: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.topic.append(
                TopicId(arg1.value),
                TopicEventPayload(arg2.toHotObject())
            )

            null to ctx.tableCreate(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "topic_id" to KuaString(res.topicId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}