package io.hamal.extension.std.sys


import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.admin.MetricData

class GetMetricFunction(
    private val hubSdk: HubSdk
) : Function0In1Out<StringType>(
    FunctionOutput1Schema(StringType::class)
) {

    override fun invoke(ctx: FunctionContext): StringType {
        val z: MetricData = hubSdk.metric.get()

        return StringType("coming soon")
    }
}

