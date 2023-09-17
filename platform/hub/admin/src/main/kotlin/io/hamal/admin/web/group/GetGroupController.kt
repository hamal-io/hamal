package io.hamal.admin.web.group

import io.hamal.core.adapter.group.GetGroup
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminGroup
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetGroupController(private val getGroup: GetGroup) {
    @GetMapping("/v1/groups/{groupId}")
    fun getGroup(
        @PathVariable("groupId") groupId: GroupId,
    ): ResponseEntity<AdminGroup> {
        return getGroup(groupId) { group ->
            ResponseEntity.ok(
                AdminGroup(
                    id = group.id,
                    name = group.name,
                )
            )
        }
    }
}