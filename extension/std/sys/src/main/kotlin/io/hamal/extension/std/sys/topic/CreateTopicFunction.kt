package io.hamal.extension.std.sys.topic

import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId

class CreateTopicFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType> {
        try {
            val r = CreateTopicReq(
                name = TopicName(arg1.getString("name")),
            )
            val res = templateSupplier()
                .post("/v1/topics")
                .body(r)
                .execute(ApiSubmittedReqWithId::class)

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