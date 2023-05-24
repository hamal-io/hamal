package io.hamal.backend.infra.usecase.query

import io.hamal.backend.repository.api.domain.trigger.Trigger
import io.hamal.lib.domain.ddd.QueryManyUseCase
import io.hamal.lib.domain.ddd.QueryOneUseCase
import io.hamal.lib.domain.vo.TriggerId

object TriggerQuery {

    data class GetTrigger(
        val triggerId: TriggerId
    ) : QueryOneUseCase<Trigger>


    data class ListTrigger(
        val afterId: TriggerId,
        val limit: Int
    ) : QueryManyUseCase<Trigger>

}