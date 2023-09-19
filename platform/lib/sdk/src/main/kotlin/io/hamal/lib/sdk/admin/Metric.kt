package io.hamal.lib.sdk.admin

import io.hamal.lib.http.*
import io.hamal.lib.sdk.fold

import kotlinx.serialization.Serializable


@Serializable
data class AdminMetrics(
    val time: Long,
    val events: List<Count>
) {
    @Serializable
    data class Count(
        val name: String,
        val value: Int
    )
}

interface AdminMetricService {
    fun get(): AdminMetrics
}

internal class DefaultAdminMetricService(
    private val template: HttpTemplate
) : AdminMetricService {

    override fun get() =
        template.get("/v1/metrics/json").header("Content-Type", "application/json").execute().fold(AdminMetrics::class)

}
