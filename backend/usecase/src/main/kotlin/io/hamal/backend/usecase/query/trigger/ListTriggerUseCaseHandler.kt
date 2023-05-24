package io.hamal.backend.usecase.query.trigger

import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.usecase.query.TriggerQuery
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListTriggerUseCaseHandler(
    val triggerQueryRepository: TriggerQueryRepository
) : QueryManyUseCaseHandler<Trigger, TriggerQuery.ListTrigger>(TriggerQuery.ListTrigger::class) {
    override fun invoke(useCase: TriggerQuery.ListTrigger): List<Trigger> {
        return triggerQueryRepository.list(useCase.afterId, useCase.limit)
    }
}