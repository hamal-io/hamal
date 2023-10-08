package io.hamal.api.web.group

import io.hamal.core.adapter.GetGroupPort
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiGroup
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetGroupController(private val getGroup: GetGroupPort) {
    @GetMapping("/v1/groups/{groupId}")
    fun getGroup(
        @PathVariable("groupId") groupId: GroupId,
    ): ResponseEntity<ApiGroup> {
        return getGroup(groupId) { group ->
            ResponseEntity.ok(
                ApiGroup(
                    id = group.id,
                    name = group.name,
                )
            )
        }
    }
}