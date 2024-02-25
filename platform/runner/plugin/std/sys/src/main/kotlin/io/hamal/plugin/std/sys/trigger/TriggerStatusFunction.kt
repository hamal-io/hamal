package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTableMap
import io.hamal.lib.sdk.ApiSdk

class TriggerActivateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTableMap, KuaError, KuaTableMap>(
    FunctionInput1Schema(KuaTableMap::class),
    FunctionOutput2Schema(KuaError::class, KuaTableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap): Pair<KuaError?, KuaTableMap?> {
        return try {
            val res = sdk.trigger.activate(
                TriggerId(arg1.getString("trigger_id"))
            )

            null to ctx.toMap(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "trigger_id" to KuaString(res.triggerId.value.value.toString(16)),
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}

class TriggerDeactivateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTableMap, KuaError, KuaTableMap>(
    FunctionInput1Schema(KuaTableMap::class),
    FunctionOutput2Schema(KuaError::class, KuaTableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap): Pair<KuaError?, KuaTableMap?> {
        return try {
            val res = sdk.trigger.deactivate(
                TriggerId(arg1.getString("trigger_id"))
            )

            null to ctx.toMap(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "trigger_id" to KuaString(res.triggerId.value.value.toString(16)),
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}