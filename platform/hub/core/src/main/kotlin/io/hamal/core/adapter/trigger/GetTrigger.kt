package io.hamal.core.adapter.trigger

import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.Topic
import org.springframework.stereotype.Component

@Component
class GetTrigger(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {

    operator fun <T : Any> invoke(
        triggerId: TriggerId,
        responseHandler: (Trigger, Func, Namespace, Topic?) -> T
    ): T {
        val trigger = triggerQueryRepository.get(triggerId)
        val func = funcQueryRepository.get(trigger.funcId)
        val namespace = namespaceQueryRepository.get(trigger.namespaceId)
        val topic = if (trigger is EventTrigger) {
            eventBrokerRepository.getTopic(trigger.topicId)
        } else {
            null
        }
        
        return responseHandler(
            trigger,
            func,
            namespace,
            topic
        )
    }
}