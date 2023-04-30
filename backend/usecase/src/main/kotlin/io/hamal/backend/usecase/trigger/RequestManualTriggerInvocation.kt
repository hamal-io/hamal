package io.hamal.backend.usecase.trigger

import io.hamal.backend.core.model.InvokedTrigger
import io.hamal.backend.core.port.notification.NotifyDomainPort
import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.usecase.InvokeUseCasePort
import io.hamal.lib.ddd.usecase.RequestOneUseCase
import io.hamal.lib.ddd.usecase.RequestOneUseCaseHandler
import io.hamal.lib.vo.TriggerId
import io.hamal.lib.vo.port.GenerateDomainIdPort
import java.util.concurrent.atomic.AtomicInteger

var counter = AtomicInteger(0)

data class ManualTriggerInvocation(
    override val requestId: RequestId,
    override val shard: Shard,
    val triggerId: TriggerId
) : RequestOneUseCase<InvokedTrigger.Manual> {

    class Operation(
        private val invoke: InvokeUseCasePort,
        private val notifyDomain: NotifyDomainPort,
        private val generateDomainId: GenerateDomainIdPort,
    ) : RequestOneUseCaseHandler<InvokedTrigger.Manual, ManualTriggerInvocation>(ManualTriggerInvocation::class) {

        override fun invoke(useCase: ManualTriggerInvocation): InvokedTrigger.Manual {
            TODO()
//            val trigger = invoke(GetTriggerUseCase(useCase.triggerId))
//            val definition =
//                invoke(GetJobDefinitionUseCase(trigger.jobDefinitionId)) // required later to get inputs/secrets
//            val trigger = DefaultTriggerStore.triggers[useCase.triggerId]!!
//            val definition = DefaultDefinitionStore.jobDefinitions[trigger.jobDefinitionId]!!
//
////            notifyDomainPort.invoke(
////                Scheduled(
////                    id = generateDomainId(useCase.shard, ::JobId),
////                    shard = Shard(1),
////                    inputs = counter.incrementAndGet()
////                )
////            )
//
//
//            val result = InvokedTrigger.Manual(
//                id = InvokedTriggerId(Snowflake.Id(1)),
//                trigger = Trigger.ManualTrigger(
//                    id = TriggerId(Snowflake.Id(2)),
//                    reference = TriggerReference("some-ref"),
//                    jobDefinitionId = definition.id,
//                ),
//                invokedAt = InvokedAt(TimeUtils.now()),
//                invokedBy = AccountId(Snowflake.Id(123))
//            )
//
//            notifyDomain(
//                TriggerDomainNotification.Invoked(
//                    invokedTrigger = result,
//                    shard = useCase.shard,
//                )
//            )
//
//            return result
        }
    }
}