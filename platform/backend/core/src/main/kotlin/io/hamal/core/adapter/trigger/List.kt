package io.hamal.core.adapter.trigger

import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.stereotype.Component

fun interface TriggerListPort {
    operator fun invoke(query: TriggerQuery): List<Trigger>
}

@Component
class TriggerListAdapter(
    private val triggerQueryRepository: TriggerQueryRepository
) : TriggerListPort {
    override fun invoke(query: TriggerQuery): List<Trigger> = triggerQueryRepository.list(query)
}
