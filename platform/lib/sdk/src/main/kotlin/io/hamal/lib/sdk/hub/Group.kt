package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.http.HttpTemplate
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

interface GroupService

internal class DefaultGroupService(
    private val httpTemplate: HttpTemplate
) : GroupService