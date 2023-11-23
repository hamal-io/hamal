package io.hamal.plugin.std.sys.trigger

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class TriggerActivateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.trigger.activate(
                arg1.findString("flow_id")?.let { TriggerId(SnowflakeId(it)) } ?: ctx[TriggerId::class]
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "trigger_id" to StringType(res.triggerId.value.value.toString(16)),
                )
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}

class TriggerDeactivateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.trigger.deactivate(
                arg1.findString("flow_id")?.let { TriggerId(SnowflakeId(it)) } ?: ctx[TriggerId::class]
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "trigger_id" to StringType(res.triggerId.value.value.toString(16)),
                )
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}