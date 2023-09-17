package io.hamal.admin.web.group

import io.hamal.core.adapter.group.ListGroup
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminGroupList
import io.hamal.repository.api.GroupQueryRepository.GroupQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListGroupController(private val listGroup: ListGroup) {
    @GetMapping("/v1/groups")
    fun listGroup(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: GroupId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminGroupList> {
        return listGroup(
            GroupQuery(
                afterId = afterId,
                limit = limit
            )
        ) { groups ->
            ResponseEntity.ok(AdminGroupList(
                groups.map { group ->
                    AdminGroupList.Group(
                        id = group.id,
                        name = group.name
                    )
                }
            ))
        }
    }
}