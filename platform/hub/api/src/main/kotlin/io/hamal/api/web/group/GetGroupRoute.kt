package io.hamal.api.web.group

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubGroup
import io.hamal.repository.api.GroupQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetGroupRoute(
    private val groupQueryRepository: GroupQueryRepository,
) {
    @GetMapping("/v1/groups/{groupId}")
    fun getGroup(
        @PathVariable("groupId") groupId: GroupId,
    ): ResponseEntity<HubGroup> {
        val result = groupQueryRepository.get(groupId)
        return ResponseEntity.ok(result.let {
            HubGroup(
                id = it.id,
                name = it.name,
            )
        })
    }
}