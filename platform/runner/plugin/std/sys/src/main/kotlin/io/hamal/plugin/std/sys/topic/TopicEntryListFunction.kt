package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toArray
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.toKua
import io.hamal.lib.sdk.ApiSdk

class TopicEntryListFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable.Array>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Array::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable.Array?> {
        return try {
            null to ctx.toArray(
                sdk.topic.events(TopicId(arg1.value))
                    .map { entry -> entry.payload.value.toKua(ctx) }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}