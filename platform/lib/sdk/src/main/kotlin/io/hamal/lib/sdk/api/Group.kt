package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.http.HttpTemplate

data class ApiGroupList(
    val groups: List<Group>
) {
    data class Group(
        val id: GroupId,
        val name: GroupName
    )
}

data class ApiGroup(
    val id: GroupId,
    val name: GroupName,
)

interface ApiGroupService

internal class ApiGroupServiceImpl(
    private val template: HttpTemplate
) : ApiGroupService