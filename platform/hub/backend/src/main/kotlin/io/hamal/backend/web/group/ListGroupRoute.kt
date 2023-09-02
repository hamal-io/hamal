package io.hamal.backend.web.group

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.domain.ApiGroupList
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
    ): ResponseEntity<ApiGroupList> {
        val result = groupQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }

        return ResponseEntity.ok(ApiGroupList(
            result.map { group ->
                ApiGroupList.ApiSimpleGroup(
                    id = group.id,
                    name = group.name
                )
            }
        ))
    }
}