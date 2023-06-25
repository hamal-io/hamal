package io.hamal.backend.repository.memory.record

import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.lib.domain.EventTrigger
import io.hamal.lib.domain.FixedRateTrigger
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain.vo.TriggerId


object MemoryTriggerRepository : TriggerCmdRepository, TriggerQueryRepository {
    override fun create(cmd: TriggerCmdRepository.CreateFixedRateCmd): FixedRateTrigger {
        TODO("Not yet implemented")
    }

    override fun create(cmd: TriggerCmdRepository.CreateEventCmd): EventTrigger {
        TODO("Not yet implemented")
    }

    override fun find(triggerId: TriggerId): Trigger? {
        TODO("Not yet implemented")
    }

    override fun list(block: TriggerQueryRepository.Query.() -> Unit): List<Trigger> {
        //FIXME
        return listOf()
    }
}