package io.hamal.backend.application.trigger

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.Scheduled
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.TimeUtils
import io.hamal.lib.vo.*
import io.hamal.lib.vo.port.GenerateDomainIdPort
import java.util.concurrent.atomic.AtomicInteger

var counter = AtomicInteger(0)

data class InvokeManualTriggerUseCase(
    val regionId: RegionId,
    val triggerId: TriggerId
) : ExecuteOneUseCase<InvokedTrigger.Manual> {

    class Operation(
        private val notifyDomainPort: NotifyDomainPort,
        private val generateDomainId: GenerateDomainIdPort,
    ) : ExecuteOneUseCaseOperation<InvokedTrigger.Manual, InvokeManualTriggerUseCase>(InvokeManualTriggerUseCase::class) {

        override fun invoke(useCase: InvokeManualTriggerUseCase): InvokedTrigger.Manual {
            notifyDomainPort.invoke(
                Scheduled(
                    id = generateDomainId(useCase.regionId, ::JobId),
                    regionId = RegionId(1),
                    inputs = counter.incrementAndGet()
                )
            )

            return InvokedTrigger.Manual(
                id = InvokedTriggerId(Snowflake.Id(1)),
                trigger = Trigger.ManualTrigger(
                    id = TriggerId(Snowflake.Id(2)),
                    reference = TriggerReference("some-ref"),
                    jobDefinitionId = JobDefinitionId(Snowflake.Id(3)),
                ),
                invokedAt = InvokedAt(TimeUtils.now()),
                invokedBy = AccountId(Snowflake.Id(123))
            )
        }
    }
}