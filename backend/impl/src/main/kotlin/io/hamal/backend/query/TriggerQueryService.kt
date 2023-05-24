package io.hamal.backend.query

import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.vo.TriggerId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TriggerQueryService(
    @Autowired val triggerQueryRepository: TriggerQueryRepository
) {
    fun get(triggerId: TriggerId): Trigger {
        return triggerQueryRepository.find(triggerId)!! //FIXME require and proper error message
    }

    fun find(triggerId: TriggerId): Trigger? {
        return triggerQueryRepository.find(triggerId)
    }

    fun list(afterId: TriggerId, limit: Int): List<Trigger> {
        return triggerQueryRepository.list(afterId, limit)
    }

}