package io.hamal.lib.sdk.admin

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

interface AdminMetricService {
    fun get(): MetricData
}

internal class DefaultAdminMetricService(
    private val template: HttpTemplate
) : AdminMetricService {

    override fun get(): MetricData {
        TODO()
        val http = template.get("/v1/metrics/json")
            .header("Content-Type", "application/json")
            .execute()
        //val obj = http.fold(MetricData::class)
        return MetricData(1, mutableListOf(MetricData.Count("sack", 1)))
    }
}
