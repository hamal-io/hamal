package io.hamal.api.http.controller.topic

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.TopicCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.TopicGroupCreateRequested
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.sdk.api.ApiTopicCreateRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TopicCreateController(
    private val retry: Retry,
    private val createTopic: TopicCreatePort
) {
    @PostMapping("/v1/groups/{groupId}/topics")
    fun createGroupTopic(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiTopicCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        createTopic(groupId, req, TopicGroupCreateRequested::accepted)
    }
}