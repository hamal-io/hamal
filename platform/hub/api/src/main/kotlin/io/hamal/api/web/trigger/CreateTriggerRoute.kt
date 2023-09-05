package io.hamal.api.web.trigger

import io.hamal.api.req.SubmitRequest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val request: SubmitRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @PostMapping("/v1/groups/{groupId}/triggers")
    fun createTrigger(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createTrigger: CreateTriggerReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        ensureFuncExists(createTrigger)
        ensureTopicExists(createTrigger)
        ensureNamespaceExist(createTrigger)

        val result = request(createTrigger)
        return ResponseEntity(result.let {
            HubSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
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

    private fun ensureNamespaceExist(createTriggerReq: CreateTriggerReq) {
        createTriggerReq.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}
