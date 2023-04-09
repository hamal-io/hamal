package io.hamal.module.launchpad.application.trigger

import io.hamal.lib.ddd.port.NotifyDomainPort
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.base.RegionId
import io.hamal.lib.domain_notification.JobDomainNotification
import java.util.*

var counter = 0

data class InvokeManualTriggerUseCase(
    val regionId: RegionId,
    val triggerId: TriggerId
) : CommandUseCase {


    class Operation(
        val notifyDomainPort: NotifyDomainPort<JobDomainNotification>
    ) : CommandUseCaseOperation.NoResultImpl<InvokeManualTriggerUseCase>(
        InvokeManualTriggerUseCase::class
    ) {
        override fun noResult(useCase: InvokeManualTriggerUseCase) {
            notifyDomainPort.invoke(
                JobDomainNotification.Scheduled(
                    id = JobId(UUID.randomUUID().toString()),
                    regionId = useCase.regionId,
                    inputs = counter++
                )
            )
        }
    }
}