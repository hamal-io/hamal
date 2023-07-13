package io.hamal.backend.instance.web.trigger

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.time.*

@RestController
class CreateTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: LogBrokerRepository<*>,
    private val request: SubmitRequest
) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestBody createTrigger: CreateTriggerReq
    ): ResponseEntity<SubmittedCreateTriggerReq> {
        ensureFuncExists(createTrigger)
        ensureTopicExists(createTrigger)

        val result = request(createTrigger)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
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
