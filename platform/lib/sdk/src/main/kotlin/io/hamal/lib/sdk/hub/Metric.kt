package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.fold
import kotlinx.serialization.Serializable


@Serializable
data class MetricType(
    val time: Long,
    val map: LinkedHashMap<String, Int>
)

interface HubMetricService {
    fun get(groupId: GroupId) : MetricType
}


internal class DefaultMetricService(
    private val template: HttpTemplate
) : HubMetricService {

    override fun get(groupId: GroupId) =
        template.post("/v1/groups/{groupId}/metrics")
            .path("groupId", groupId)
            .execute()
            .fold(MetricType::class)


}
