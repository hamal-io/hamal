package io.hamal.plugin.std.sys.topic.trigger

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import kotlin.time.Duration

class TriggerCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.trigger.create(
                arg1.findString("flow_id")?.let { FlowId(SnowflakeId(it)) } ?: ctx[FlowId::class],
                ApiTriggerCreateReq(
                    type = TriggerType.valueOf(arg1.getString("type")),
                    funcId = FuncId(SnowflakeId(arg1.getString("func_id"))),
                    name = TriggerName(arg1.getString("name")),
                    inputs = TriggerInputs(),
                    duration = if (arg1.type("duration") == StringType::class) {
                        Duration.parseIsoString(arg1.getString("duration"))
                    } else {
                        null
                    },
                    topicId = if (arg1.type("topic_id") == StringType::class) {
                        TopicId(SnowflakeId(arg1.getString("topic_id")))
                    } else {
                        null
                    },
                    hookId = if (arg1.type("hook_id") == StringType::class) {
                        HookId(SnowflakeId(arg1.getString("hook_id")))
                    } else {
                        null
                    },
                    cron = if (arg1.type("cron") == StringType::class) {
                        CronPattern(arg1.getString("cron"))
                    } else {
                        null
                    }

                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "trigger_id" to StringType(res.triggerId.value.value.toString(16)),
                    "group_id" to StringType(res.groupId.value.value.toString(16)),
                    "flow_id" to StringType(res.flowId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}