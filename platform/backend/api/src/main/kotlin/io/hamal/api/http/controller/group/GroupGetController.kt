package io.hamal.api.http.controller.group

import io.hamal.core.adapter.GroupGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiGroup
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GroupGetController(
    private val retry: Retry,
    private val getGroup: GroupGetPort
) {
    @GetMapping("/v1/groups/{groupId}")
    fun getGroup(
        @PathVariable("groupId") groupId: GroupId,
    ): ResponseEntity<ApiGroup> {
        return retry {
            getGroup(groupId) { group ->
                ResponseEntity.ok(
                    ApiGroup(
                        id = group.id,
                        name = group.name,
                    )
                )
            }
        }
    }
}