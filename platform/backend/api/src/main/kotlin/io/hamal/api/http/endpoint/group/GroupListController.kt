package io.hamal.api.http.endpoint.group

import io.hamal.api.http.auth.AuthContextHolder
import io.hamal.core.adapter.GroupListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiGroupList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GroupListController(
    private val list: GroupListPort
) {
    @GetMapping("/v1/groups")
    fun listGroup(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: GroupId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
    ): ResponseEntity<ApiGroupList> {
        return list(AuthContextHolder.get().accountId) { groups ->
            ResponseEntity.ok(
                ApiGroupList(
                    groups = groups.map { group ->
                        ApiGroupList.Group(
                            id = group.id,
                            name = group.name
                        )
                    }
                )
            )
        }
    }
}