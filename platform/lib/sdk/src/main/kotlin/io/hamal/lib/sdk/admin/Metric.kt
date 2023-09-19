package io.hamal.lib.sdk.admin

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable


@Serializable
data class AdminMetrics(
    val time: Long,
    val map: LinkedHashMap<String, Int>
)

interface AdminMetricService {
    fun clear()
    fun get(): AdminMetrics
}


internal class DefaultAdminMetricService(
    private val template: HttpTemplate
) : AdminMetricService {

    override fun get() =
        template.get("/v1/metrics/json")
            .execute()
            .fold(AdminMetrics::class)

    override fun clear() {
        template.post("/v1/metrics/clear")
            .execute()
    }
}
