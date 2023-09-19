package io.hamal.extension.std.sys


import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.admin.AdminMetricService
import io.hamal.lib.sdk.admin.AdminMetrics

class GetMetricFunction(
    private val hubSdk: HubSdk
) : Function0In1Out<StringType>(
    FunctionOutput1Schema(StringType::class)
) {

    override fun invoke(ctx: FunctionContext): StringType {
        val z: AdminMetrics = hubSdk.metric.get()

        return StringType(z.time.toString())
    }
}

