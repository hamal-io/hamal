package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository
import org.springframework.stereotype.Component

fun interface TriggerGetPort {
    operator fun invoke(triggerId: TriggerId): Trigger
}

@Component
class TriggerGetAdapter(
    private val triggerQueryRepository: TriggerQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : TriggerGetPort {
    override fun invoke(triggerId: TriggerId): Trigger = ensureAccess(triggerQueryRepository.get(triggerId))
}