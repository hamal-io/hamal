package io.hamal.backend.application.trigger

import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.domain.vo.JobId
import io.hamal.lib.domain.vo.RegionId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.port.GenerateDomainIdPort
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.core.notification.Scheduled
import java.util.concurrent.atomic.AtomicInteger

var counter = AtomicInteger(0)

data class InvokeManualTriggerUseCase(
    val regionId: RegionId,
    val triggerId: TriggerId
) : CommandUseCase {

    class Operation(
        val notifyDomainPort: NotifyDomainPort,
        val generateDomainId: GenerateDomainIdPort,
    ) : CommandUseCaseOperation.NoResultImpl<InvokeManualTriggerUseCase>(
        InvokeManualTriggerUseCase::class
    ) {
        override fun noResult(useCase: InvokeManualTriggerUseCase) {
            notifyDomainPort.invoke(
                Scheduled(
                    id = generateDomainId(useCase.regionId, ::JobId),
                    regionId = RegionId(1),
                    inputs = counter.incrementAndGet()
                )
            )
        }
    }
}