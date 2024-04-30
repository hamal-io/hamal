package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueString

class TopicGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, KuaError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.topic.get(TopicId(arg1.stringValue))
                .let { topic ->
                    ctx.tableCreate(
                        "id" to ValueString(topic.id.value.value.toString(16)),
                        "name" to ValueString(topic.name.value),
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}