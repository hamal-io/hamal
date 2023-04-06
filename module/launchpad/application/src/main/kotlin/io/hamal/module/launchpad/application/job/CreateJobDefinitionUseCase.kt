package io.hamal.module.launchpad.application.job

import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobDefinitionId
import io.hamal.lib.domain.vo.JobReference
import io.hamal.lib.domain.vo.Region
import io.hamal.module.launchpad.core.job.model.JobDefinition
import java.util.*


data class CreateJobDefinitionUseCase(
    val region: Region,
    val reference: JobReference
) : CommandUseCase {

    class Operation() : CommandUseCaseOperation<JobDefinition, CreateJobDefinitionUseCase>(
        JobDefinition::class, CreateJobDefinitionUseCase::class
    ) {
        override fun invoke(useCase: CreateJobDefinitionUseCase): List<JobDefinition> {
            return listOf(
                JobDefinition(
                    id = JobDefinitionId(UUID.randomUUID().toString()),
                    region = useCase.region
                )
            )
        }
    }
}