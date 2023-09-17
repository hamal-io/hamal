package io.hamal.core.adapter.trigger

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.Topic
import org.springframework.stereotype.Component


@Component
class ListTriggers(
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository
) {

    operator fun <T : Any> invoke(
        groupId: GroupId,
        query: TriggerQueryRepository.TriggerQuery,
        responseHandler: (
            triggers: List<Trigger>,
            funcs: Map<FuncId, Func>,
            namespaces: Map<NamespaceId, Namespace>,
            topics: Map<TopicId, Topic>
        ) -> T
    ): T {

        val triggers = triggerQueryRepository.list(query)

        val namespaces = namespaceQueryRepository.list(triggers.map { it.namespaceId })
            .associateBy { it.id }

        val funcs = funcQueryRepository.list(triggers.map { it.funcId })
            .associateBy { it.id }

        val topics = eventBrokerRepository.list(triggers.filterIsInstance<EventTrigger>().map { it.topicId })
            .associateBy { it.id }

        return responseHandler(triggers, funcs, namespaces, topics)
    }
}
