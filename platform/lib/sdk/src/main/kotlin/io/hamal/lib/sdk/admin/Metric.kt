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
    fun get(): AdminMetrics
}


internal class DefaultAdminMetricService(
    private val template: HttpTemplate
) : AdminMetricService {

    override fun get() =
        template.get("/v1/metrics")
            .execute()
            .fold(AdminMetrics::class)


}
