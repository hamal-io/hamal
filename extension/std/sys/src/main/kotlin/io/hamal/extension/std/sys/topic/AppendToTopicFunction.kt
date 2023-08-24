package io.hamal.extension.std.sys.topic

import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId

class AppendToTopicFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function2In2Out<StringType, MapType, ErrorType, MapType>(
    FunctionInput2Schema(StringType::class, MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: MapType): Pair<ErrorType?, MapType?> {
        try {
            val res = templateSupplier()
                .post("/v1/topics/{topicId}/events")
                .path("topicId", arg1.value)
                .body(EventPayload(arg2))
                .execute(ApiSubmittedReqWithId::class)

            return null to MapType().also {
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