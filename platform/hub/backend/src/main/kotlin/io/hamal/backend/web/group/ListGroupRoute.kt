package io.hamal.backend.web.group

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubGroupList
import io.hamal.repository.api.GroupQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListGroupRoute(
    private val groupQueryRepository: GroupQueryRepository
) {
    @GetMapping("/v1/groups")
    fun listGroup(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: GroupId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubGroupList> {
        val result = groupQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }

        return ResponseEntity.ok(HubGroupList(
            result.map { group ->
                HubGroupList.Group(
                    id = group.id,
                    name = group.name
                )
            }
        ))
    }
}