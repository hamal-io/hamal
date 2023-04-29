package io.hamal.backend.application.trigger

import io.hamal.backend.application.DummyDb
import io.hamal.backend.core.model.Trigger
import io.hamal.lib.ddd.usecase.QueryOneUseCase
import io.hamal.lib.ddd.usecase.QueryOneUseCaseOperation
import io.hamal.lib.vo.TriggerId

data class GetTriggerUseCase(
    val id: TriggerId
) : QueryOneUseCase<Trigger> {

    class Operation : QueryOneUseCaseOperation<Trigger, GetTriggerUseCase>(GetTriggerUseCase::class) {
        override fun invoke(useCase: GetTriggerUseCase): Trigger {
//            return await
//                .atMost(3.seconds.toJavaDuration())
//                .untilNotNull { DummyDb.triggers[useCase.id] }

            return DummyDb.triggers[useCase.id]!!
        }
    }


}