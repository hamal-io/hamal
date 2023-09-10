package io.hamal.admin.web.trigger

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminCreateTriggerReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
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
internal class CreateTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val request: SubmitAdminRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @PostMapping("/v1/groups/{groupId}/triggers")
    fun createTrigger(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createTrigger: AdminCreateTriggerReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        ensureFuncExists(createTrigger)
        ensureTopicExists(createTrigger)
        ensureNamespaceExist(createTrigger)

        val result = request(createTrigger)
        return ResponseEntity(result.let {
            AdminSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }

    private fun ensureFuncExists(createTrigger: AdminCreateTriggerReq) {
        funcQueryRepository.get(createTrigger.funcId)
    }

    private fun ensureTopicExists(createTrigger: AdminCreateTriggerReq) {
        if (createTrigger.type == TriggerType.Event) {
            requireNotNull(createTrigger.topicId) { "topicId is missing" }
            eventBrokerRepository.getTopic(createTrigger.topicId!!)
        }
    }

    private fun ensureNamespaceExist(createTriggerReq: AdminCreateTriggerReq) {
        createTriggerReq.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}
