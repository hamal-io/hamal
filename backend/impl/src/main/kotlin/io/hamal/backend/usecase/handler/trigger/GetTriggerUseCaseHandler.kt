package io.hamal.backend.usecase.handler.trigger

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.usecase.TriggerQueryUseCase.GetTrigger
import io.hamal.lib.domain.ddd.QueryOneUseCaseHandler

class GetTriggerUseCaseHandler(
    val triggerQueryRepository: TriggerQueryRepository
) : QueryOneUseCaseHandler<Trigger, GetTrigger>(GetTrigger::class) {
    override fun invoke(useCase: GetTrigger): Trigger {
        return triggerQueryRepository.find(useCase.triggerId) ?: TODO() // FIXME
    }
}