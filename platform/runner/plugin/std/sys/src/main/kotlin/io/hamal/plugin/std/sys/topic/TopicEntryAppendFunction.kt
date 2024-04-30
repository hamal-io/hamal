package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.TopicEventPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.toHotObject
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueString

class TopicEntryAppendFunction(
    private val sdk: ApiSdk
) : Function2In2Out<ValueString, KuaTable, KuaError, KuaTable>(
    FunctionInput2Schema(ValueString::class, KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: ValueString, arg2: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.topic.append(
                TopicId(arg1.stringValue),
                TopicEventPayload(arg2.toHotObject())
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.value.value.toString(16)),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}