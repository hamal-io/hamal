package io.hamal.lib.sdk.hub

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

@Serializable
data class HubGroupList(
    val groups: List<Group>
) {
    @Serializable
    data class Group(
        val id: GroupId,
        val name: GroupName
    )
}


@Serializable
data class HubGroup(
    val id: GroupId,
    val name: GroupName,
)

interface HubGroupService

internal class DefaultHubGroupService(
    private val template: HttpTemplate
) : HubGroupService