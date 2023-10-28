package io.hamal.api.http.endpoint.topic

import io.hamal.core.adapter.TopicCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.api.ApiTopicCreateReq
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
    @PostMapping("/v1/namespaces/{namespaceId}/topics")
    fun createTopic(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiTopicCreateReq
    ): ResponseEntity<ApiSubmittedReqImpl<TopicId>> = retry {
        createTopic(namespaceId, req) {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = it.namespaceId,
                        groupId = it.groupId,
                        id = it.id
                    )
                )
        }
    }
}