package io.hamal.backend.usecase.trigger

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.model.Trigger
import io.hamal.backend.core.notification.TriggerDomainNotification
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.backend.store.impl.DefaultFlowDefinitionStore
import io.hamal.backend.store.impl.DefaultTriggerStore
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.ddd.usecase.RequestOneUseCaseOperation
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.util.Snowflake
import io.hamal.lib.util.TimeUtils
import io.hamal.lib.vo.*
import io.hamal.lib.vo.port.GenerateDomainIdPort
import java.util.concurrent.atomic.AtomicInteger

var counter = AtomicInteger(0)

data class ManualTriggerInvocation(
    val shard: Shard,
    val triggerId: TriggerId
) : RequestOneUseCase<InvokedTrigger.Manual> {

    class Operation(
        private val invoke: InvokeUseCasePort,
        private val notifyDomain: NotifyDomainPort,
        private val generateDomainId: GenerateDomainIdPort,
    ) : RequestOneUseCaseOperation<InvokedTrigger.Manual, ManualTriggerInvocation>(ManualTriggerInvocation::class) {

        override fun invoke(useCase: ManualTriggerInvocation): InvokedTrigger.Manual {
//            val trigger = invoke(GetTriggerUseCase(useCase.triggerId))
//            val definition =
//                invoke(GetFlowDefinitionUseCase(trigger.flowDefinitionId)) // required later to get inputs/secrets
            val trigger = DefaultTriggerStore.triggers[useCase.triggerId]!!
            val definition = DefaultFlowDefinitionStore.flowDefinitions[trigger.flowDefinitionId]!!

//            notifyDomainPort.invoke(
//                Scheduled(
//                    id = generateDomainId(useCase.shard, ::FlowId),
//                    shard = Shard(1),
//                    inputs = counter.incrementAndGet()
//                )
//            )


            val result = InvokedTrigger.Manual(
                id = InvokedTriggerId(Snowflake.Id(1)),
                trigger = Trigger.ManualTrigger(
                    id = TriggerId(Snowflake.Id(2)),
                    reference = TriggerReference("some-ref"),
                    flowDefinitionId = definition.id,
                ),
                invokedAt = InvokedAt(TimeUtils.now()),
                invokedBy = AccountId(Snowflake.Id(123))
            )

            notifyDomain(
                TriggerDomainNotification.Invoked(
                    invokedTrigger = result,
                    shard = useCase.shard,
                )
            )

            return result
        }
    }
}