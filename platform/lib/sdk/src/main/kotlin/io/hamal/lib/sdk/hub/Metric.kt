package io.hamal.lib.sdk.hub

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable

@Serializable
data class MetricData(
    val time: Long = System.currentTimeMillis(),
    val events: MutableList<Count> = mutableListOf()
) {
    @Serializable
    data class Count(
        val name: String,
        var value: Int
    )
}

interface HubMetricService {
    fun get(): MetricData
}

internal class DefaultHubMetricService(
    private val template: HttpTemplate
) : HubMetricService {
    override fun get() = template.get("/v1/metrics/json")
        .execute()
        .fold(MetricData::class)
}
