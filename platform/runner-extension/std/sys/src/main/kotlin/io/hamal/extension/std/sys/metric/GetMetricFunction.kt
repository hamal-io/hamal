package io.hamal.extension.std.sys.metric


import io.hamal.lib.kua.function.Function0In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.sdk.HubSdk
import io.hamal.lib.sdk.admin.MetricData


class GetMetricFunction(
    private val hubSdk: HubSdk
) : Function0In1Out<MapType>(
    FunctionOutput1Schema(MapType::class)
) {

    override fun invoke(ctx: FunctionContext): MapType {
        val data: MetricData = hubSdk.metric.get()
        val map = MapType()
        map.set("time", data.time)
        if (!data.events.isEmpty()) {
            for (i in data.events) {
                map.set(i.name.replace("::", "_"), i.value)
            }
        }
        return map
    }
}

