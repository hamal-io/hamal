package io.hamal.backend.usecase.query.trigger

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.usecase.query.TriggerQuery
import io.hamal.lib.domain.ddd.QueryManyUseCaseHandler

class ListTriggerUseCaseHandler(
    val triggerQueryRepository: TriggerQueryRepository
) : QueryManyUseCaseHandler<Trigger, io.hamal.backend.usecase.query.TriggerQuery.ListTrigger>(io.hamal.backend.usecase.query.TriggerQuery.ListTrigger::class) {
    override fun invoke(useCase: io.hamal.backend.usecase.query.TriggerQuery.ListTrigger): List<Trigger> {
        return triggerQueryRepository.list(useCase.afterId, useCase.limit)
    }
}