package io.hamal.admin.web.group

import io.hamal.core.adapter.ListGroupsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiGroupList
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListGroupsController(private val listGroup: ListGroupsPort) {
    @GetMapping("/v1/groups")
    fun listGroup(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: GroupId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") groupIds: List<GroupId>
    ): ResponseEntity<ApiGroupList> {
        return listGroup(
            GroupQuery(
                afterId = afterId,
                limit = limit,
                groupIds = groupIds
            )
        ) { groups ->
            ResponseEntity.ok(ApiGroupList(
                groups.map { group ->
                    ApiGroupList.Group(
                        id = group.id,
                        name = group.name
                    )
                }
            ))
        }
    }
}