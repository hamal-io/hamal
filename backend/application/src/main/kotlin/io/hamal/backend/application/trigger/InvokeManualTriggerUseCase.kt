package io.hamal.backend.application.trigger

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.lib.ddd.usecase.CommandUseCase
import io.hamal.lib.ddd.usecase.CommandUseCaseOperation
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.TimeUtils
import io.hamal.lib.vo.*
import java.util.concurrent.atomic.AtomicInteger

var counter = AtomicInteger(0)

data class InvokeManualTriggerUseCase(
    val regionId: RegionId,
    val triggerId: TriggerId
) : CommandUseCase<InvokedTrigger> {

    class Operation(
//        private val notifyDomainPort: NotifyDomainPort,
//        private val generateDomainId: GenerateDomainIdPort,
    ) : CommandUseCaseOperation<InvokedTrigger, InvokeManualTriggerUseCase>(InvokeManualTriggerUseCase::class) {

        override fun invoke(useCase: InvokeManualTriggerUseCase): InvokedTrigger {
//            notifyDomainPort.invoke(
//                Scheduled(
//                    id = generateDomainId(useCase.regionId, ::JobId),
//                    regionId = RegionId(1),
//                    inputs = counter.incrementAndGet()
//                )
//            )

            return InvokedTrigger(
                id = InvokedTriggerId(Snowflake.Id(1)),
                triggerId = TriggerId(Snowflake.Id(2)),
                reference = TriggerReference("some-ref"),
                jobDefinitionId = JobDefinitionId(Snowflake.Id(3)),
                accountId = AccountId(Snowflake.Id(4)),
                invokedAt = InvokedAt(TimeUtils.now())
            )
        }
    }
}