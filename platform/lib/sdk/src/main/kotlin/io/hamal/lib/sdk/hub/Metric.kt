package io.hamal.lib.sdk.hub

import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

interface HubMetricService {
    fun get()
}

@Serializable
data class MetricDataType (
    val time: Long,
    val map: LinkedHashMap<String, Int>
)

class DefaultMetricService(
    private val template: HttpTemplate
):HubMetricService{

    override fun get() {
        template.post("/v1/metrics/test")
            .body("Guten Tag")
            .execute()
    }
}