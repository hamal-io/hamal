package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class TriggerDeleteFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.trigger.delete(TriggerId(arg1.stringValue))

            null to ctx.tableCreate(
                "id" to ValueString(res.id.stringValue),
                "request_id" to ValueString(res.requestId.value.stringValue),
                "request_status" to ValueString(res.requestStatus.stringValue)
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}