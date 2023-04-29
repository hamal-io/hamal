package io.hamal.backend.application.trigger

import io.hamal.backend.application.job.GetJobDefinitionUseCase
import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.lib.ddd.usecase.ExecuteOneUseCase
import io.hamal.lib.ddd.usecase.ExecuteOneUseCaseOperation
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
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
        private val invoke: InvokeUseCasePort,
        private val notifyDomain: NotifyDomainPort,
        private val generateDomainId: GenerateDomainIdPort,
    ) : ExecuteOneUseCaseOperation<InvokedTrigger.Manual, InvokeManualTriggerUseCase>(InvokeManualTriggerUseCase::class) {

        override fun invoke(useCase: InvokeManualTriggerUseCase): InvokedTrigger.Manual {
            val trigger = invoke(GetTriggerUseCase(useCase.triggerId))
            val definition =
                invoke(GetJobDefinitionUseCase(trigger.jobDefinitionId)) // required later to get inputs/secrets

//            notifyDomainPort.invoke(
//                Scheduled(
//                    id = generateDomainId(useCase.regionId, ::JobId),
//                    regionId = RegionId(1),
//                    inputs = counter.incrementAndGet()
//                )
//            )


            val result = InvokedTrigger.Manual(
                id = InvokedTriggerId(Snowflake.Id(1)),
                trigger = Trigger.ManualTrigger(
                    id = TriggerId(Snowflake.Id(2)),
                    reference = TriggerReference("some-ref"),
                    jobDefinitionId = definition.id,
                ),
                invokedAt = InvokedAt(TimeUtils.now()),
                invokedBy = AccountId(Snowflake.Id(123))
            )

            notifyDomain(
                TriggerDomainNotification.Invoked(
                    invokedTrigger = result,
                    regionId = useCase.regionId,
                )
            )

            return result
        }
    }
}