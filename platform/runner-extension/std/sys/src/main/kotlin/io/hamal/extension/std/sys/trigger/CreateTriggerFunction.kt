package io.hamal.extension.std.sys.trigger

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.vo.*
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import kotlin.time.Duration

class CreateTriggerFunction(
    private val httpTemplate: HttpTemplate
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType> {
        try {

            val type = TriggerType.valueOf(arg1.getString("type"))

            val namespaceId = if (arg1.type("namespace_id") == StringType::class) {
                NamespaceId(SnowflakeId(arg1.getString("namespace_id")))
            } else {
                null
            }

            val duration = if (arg1.type("duration") == StringType::class) {
                Duration.parseIsoString(arg1.getString("duration"))
            } else {
                null
            }

            val topicId = if (arg1.type("topic_id") == StringType::class) {
                TopicId(SnowflakeId(arg1.getString("topic_id")))
            } else {
                null
            }

            val r = CreateTriggerReq(
                type = type,
                namespaceId = namespaceId,
                funcId = FuncId(SnowflakeId(arg1.getString("func_id"))),
                name = TriggerName(arg1.getString("name")),
                inputs = TriggerInputs(),
                duration = duration,
                topicId = topicId
            )

            val res = httpTemplate.post("/v1/triggers")
                .body(r)
                .execute(HubSubmittedReqWithId::class)

            return null to MapType(
                mutableMapOf(
                    "req_id" to StringType(res.reqId.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "id" to StringType(res.id.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}