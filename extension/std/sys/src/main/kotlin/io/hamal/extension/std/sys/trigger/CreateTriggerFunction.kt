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
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
import kotlin.time.Duration

class CreateTriggerFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<TableProxyMap, ErrorType, TableProxyMap>(
    FunctionInput1Schema(TableProxyMap::class),
    FunctionOutput2Schema(ErrorType::class, TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxyMap): Pair<ErrorType?, TableProxyMap> {
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

            val res = templateSupplier()
                .post("/v1/triggers")
                .body(r)
                .execute(ApiSubmittedReqWithId::class)

            return null to ctx.tableCreateMap(1).also {
                it["req_id"] = res.reqId
                it["status"] = res.status.name
                it["id"] = res.id
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}