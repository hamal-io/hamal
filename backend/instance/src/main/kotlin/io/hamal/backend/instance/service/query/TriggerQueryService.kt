package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain._enum.TriggerType
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

    fun list(afterId: TriggerId, types: Set<TriggerType>, limit: Int): List<Trigger> {
        return triggerQueryRepository.list {
            this.afterId = afterId
            this.types = types
            this.limit = limit
        }
    }

}