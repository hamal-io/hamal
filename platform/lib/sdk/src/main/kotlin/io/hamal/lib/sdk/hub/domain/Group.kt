package io.hamal.lib.sdk.hub.domain

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import kotlinx.serialization.Serializable

@Serializable
data class ApiGroupList(
    val groups: List<ApiSimpleGroup>
) {
    @Serializable
    data class ApiSimpleGroup(
        val id: GroupId,
        val name: GroupName
    )
}


@Serializable
data class ApiGroup(
    val id: GroupId,
    val name: GroupName,
)