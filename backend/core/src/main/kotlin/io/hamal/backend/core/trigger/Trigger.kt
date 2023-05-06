package io.hamal.backend.core.trigger

import io.hamal.backend.core.trigger.Trigger.ManualTrigger
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Trigger : DomainObject<TriggerId> {

    abstract val reference: TriggerReference
    abstract val jobDefinitionId: JobDefinitionId

    @Serializable
    data class ManualTrigger(
        override val id: TriggerId,
        override val reference: TriggerReference,
        override val jobDefinitionId: JobDefinitionId
    ) : Trigger()

}

@Serializable
sealed class InvokedTrigger : DomainObject<InvokedTriggerId> {
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: TenantId

    @Serializable
    data class Manual(
        override val id: InvokedTriggerId,
        val trigger: ManualTrigger,
        override val invokedAt: InvokedAt,
        override val invokedBy: TenantId
    ) : InvokedTrigger()

}