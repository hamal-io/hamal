package io.hamal.lib.sdk.admin

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.http.HttpTemplate
import kotlinx.serialization.Serializable

@Serializable
data class AdminGroupList(
    val groups: List<Group>
) {
    @Serializable
    data class Group(
        val id: GroupId,
        val name: GroupName
    )
}


@Serializable
data class AdminGroup(
    val id: GroupId,
    val name: GroupName,
)

interface AdminGroupService

internal class AdminGroupServiceImpl(
    private val template: HttpTemplate
) : AdminGroupService