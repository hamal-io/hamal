package io.hamal.backend.usecase.handler.trigger

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.usecase.TriggerQueryUseCase
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListTriggerUseCaseHandler(
    val triggerQueryRepository: TriggerQueryRepository
) : QueryManyUseCaseHandler<Trigger, TriggerQueryUseCase.ListTrigger>(TriggerQueryUseCase.ListTrigger::class) {
    override fun invoke(useCase: TriggerQueryUseCase.ListTrigger): List<Trigger> {
        return triggerQueryRepository.list(useCase.afterId, useCase.limit)
    }
}