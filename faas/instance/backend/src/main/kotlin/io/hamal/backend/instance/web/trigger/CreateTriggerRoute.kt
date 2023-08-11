package io.hamal.backend.instance.web.trigger

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: LogBrokerRepository,
    private val request: SubmitRequest
) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestBody createTrigger: CreateTriggerReq
    ): ResponseEntity<ApiSubmittedReqWithDomainId> {
        ensureFuncExists(createTrigger)
        ensureTopicExists(createTrigger)

        val result = request(createTrigger)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithDomainId(
                reqId = it.reqId,
                status = it.status,
                id = it.id.value
            )
        }, ACCEPTED)
    }

    private fun ensureFuncExists(createTrigger: CreateTriggerReq) {
        funcQueryRepository.get(createTrigger.funcId)
    }

    private fun ensureTopicExists(createTrigger: CreateTriggerReq) {
        if (createTrigger.type == TriggerType.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            eventBrokerRepository.getTopic(createTrigger.topicId!!)
        }
    }
}
