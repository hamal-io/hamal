package io.hamal.api.http.controller.topic

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.TopicCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.lib.sdk.api.ApiTopicCreateReq
import io.hamal.lib.domain.submitted.TopicCreateSubmitted
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
    @PostMapping("/v1/flows/{flowId}/topics")
    fun createTopic(
        @PathVariable("flowId") flowId: FlowId,
        @RequestBody req: ApiTopicCreateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        createTopic(flowId, req, TopicCreateSubmitted::accepted)
    }
}