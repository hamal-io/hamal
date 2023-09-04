package io.hamal.backend.web.trigger

import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.hub.ApiEventTrigger
import io.hamal.lib.sdk.hub.ApiFixedRateTrigger
import io.hamal.lib.sdk.hub.ApiTrigger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<ApiTrigger> {
        val result = triggerQueryRepository.get(triggerId)
        val namespace = namespaceQueryRepository.get(result.namespaceId)
        return ResponseEntity.ok(result.let {
            when (val trigger = it) {
                is FixedRateTrigger -> ApiFixedRateTrigger(
                    id = trigger.id,
                    name = trigger.name,
                    func = ApiTrigger.Func(
                        id = trigger.funcId,
                        name = funcQueryRepository.get(trigger.funcId).name
                    ),
                    namespace = ApiTrigger.Namespace(
                        id = namespace.id,
                        name = namespace.name
                    ),
                    inputs = trigger.inputs,
                    duration = trigger.duration
                )

                is EventTrigger -> ApiEventTrigger(
                    id = trigger.id,
                    name = trigger.name,
                    func = ApiTrigger.Func(
                        id = trigger.funcId,
                        name = funcQueryRepository.get(trigger.funcId).name
                    ),
                    namespace = ApiTrigger.Namespace(
                        id = namespace.id,
                        name = namespace.name
                    ),
                    inputs = trigger.inputs,
                    topic = ApiEventTrigger.Topic(
                        id = trigger.topicId,
                        name = eventBrokerRepository.getTopic(trigger.topicId).name
                    )
                )
            }
        })
    }
}