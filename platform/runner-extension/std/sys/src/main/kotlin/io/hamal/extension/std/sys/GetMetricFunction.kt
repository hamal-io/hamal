package io.hamal.extension.std.sys


import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.admin.AdminMetricService

class GetMetricFunction(
    //private val httpTemplate: HttpTemplate
    private val hubSdk: HubSdk
) : Function0In1Out<StringType>(
    FunctionOutput1Schema(StringType::class)
) {
    override fun invoke(ctx: FunctionContext): StringType {
       // val res = httpTemplate.get("/v1/metrics")
        val res: AdminMetricService = hubSdk.metric
        return StringType(res.get().toString())

    }
}