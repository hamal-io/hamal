package io.hamal.admin.web.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.admin.AdminEventTrigger
import io.hamal.lib.sdk.admin.AdminFixedRateTrigger
import io.hamal.lib.sdk.admin.AdminTrigger
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<AdminTrigger> {
        val result = triggerQueryRepository.get(triggerId)
        val namespace = namespaceQueryRepository.get(result.namespaceId)
        return ResponseEntity.ok(result.let {
            when (val trigger = it) {
                is FixedRateTrigger -> AdminFixedRateTrigger(
                    id = trigger.id,
                    name = trigger.name,
                    func = AdminTrigger.Func(
                        id = trigger.funcId,
                        name = funcQueryRepository.get(trigger.funcId).name
                    ),
                    namespace = AdminTrigger.Namespace(
                        id = namespace.id,
                        name = namespace.name
                    ),
                    inputs = trigger.inputs,
                    duration = trigger.duration
                )

                is EventTrigger -> AdminEventTrigger(
                    id = trigger.id,
                    name = trigger.name,
                    func = AdminTrigger.Func(
                        id = trigger.funcId,
                        name = funcQueryRepository.get(trigger.funcId).name
                    ),
                    namespace = AdminTrigger.Namespace(
                        id = namespace.id,
                        name = namespace.name
                    ),
                    inputs = trigger.inputs,
                    topic = AdminEventTrigger.Topic(
                        id = trigger.topicId,
                        name = eventBrokerRepository.getTopic(trigger.topicId).name
                    )
                )
            }
        })
    }
}