package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getString
import io.hamal.lib.sdk.ApiSdk

class TriggerActivateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.trigger.activate(
                TriggerId(arg1.getString("trigger_id").stringValue)
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.stringValue),
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue),
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}

class TriggerDeactivateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.trigger.deactivate(
                TriggerId(arg1.getString("trigger_id").stringValue)
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.stringValue),
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue),
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}