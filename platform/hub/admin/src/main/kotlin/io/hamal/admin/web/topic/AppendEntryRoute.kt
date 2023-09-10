package io.hamal.admin.web.topic

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.TopicEntryPayload
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.sdk.admin.AdminAppendEntryReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AppendEntryRoute(
    private val submitRequest: SubmitAdminRequest,
    private val eventBrokerRepository: BrokerRepository
) {
    @PostMapping("/v1/topics/{topicId}/entries")
    fun appendEvent(
        @PathVariable("topicId") topicId: TopicId,
        @RequestBody topAppend: TopicEntryPayload
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val topic = eventBrokerRepository.getTopic(topicId)
        val result = submitRequest(
            AdminAppendEntryReq(
                topicId = topic.id,
                payload = topAppend
            )
        )
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }
}