package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.TriggerCmdRepository
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.domain.EventTrigger
import io.hamal.backend.repository.api.domain.FixedRateTrigger
import io.hamal.backend.repository.api.domain.Trigger
import io.hamal.lib.domain.vo.TriggerId


object MemoryTriggerRepository: TriggerCmdRepository, TriggerQueryRepository{
    override fun create(cmd: TriggerCmdRepository.CreateFixedRateCmd): FixedRateTrigger {
        TODO("Not yet implemented")
    }

    override fun create(cmd: TriggerCmdRepository.CreateEventCmd): EventTrigger {
        TODO("Not yet implemented")
    }

    override fun find(triggerId: TriggerId): Trigger? {
        TODO("Not yet implemented")
    }

    override fun query(block: TriggerQueryRepository.Query.() -> Unit): List<Trigger> {
        //FIXME
        return listOf()
    }
}