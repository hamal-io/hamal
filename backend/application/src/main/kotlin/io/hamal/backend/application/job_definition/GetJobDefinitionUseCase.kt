package io.hamal.backend.application.job_definition

import io.hamal.backend.core.model.JobDefinition
import io.hamal.backend.store.impl.DefaultJobDefinitionStore
import io.hamal.lib.ddd.usecase.QueryOneUseCase
import io.hamal.lib.ddd.usecase.QueryOneUseCaseOperation
import io.hamal.lib.vo.JobDefinitionId

data class GetJobDefinitionUseCase(
    val id: JobDefinitionId
) : QueryOneUseCase<JobDefinition> {

    class Operation : QueryOneUseCaseOperation<JobDefinition, GetJobDefinitionUseCase>(GetJobDefinitionUseCase::class) {
        override fun invoke(useCase: GetJobDefinitionUseCase): JobDefinition {
//            return await
//                .atMost(3.seconds.toJavaDuration())
//                .untilNotNull { DummyDb.jobDefinitions[useCase.id] }
            return DefaultJobDefinitionStore.jobDefinitions[useCase.id]!!
        }
    }


}