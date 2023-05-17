package io.hamal.backend.core.trigger

import io.hamal.backend.core.job_definition.JobDefinition
import io.hamal.backend.core.trigger.Trigger.ManualTrigger
import io.hamal.lib.domain.DomainObject
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.vo.*
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

    @Serializable
    data class AdhocTrigger(
        override val id: TriggerId,
        override val reference: TriggerReference,
        override val jobDefinitionId: JobDefinitionId
    ) : Trigger()
}

@Serializable
sealed class InvokedTrigger : DomainObject<InvokedTriggerId> {
    abstract val jobDefinition: JobDefinition
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: Requester<TenantId>

    @Serializable
    data class Manual(
        override val id: InvokedTriggerId,
        override val jobDefinition: JobDefinition,
        val trigger: ManualTrigger,
        override val invokedAt: InvokedAt,
        override val invokedBy: Requester<TenantId>
    ) : InvokedTrigger()

    @Serializable
    data class Adhoc(
        override val id: InvokedTriggerId,
        override val jobDefinition: JobDefinition,
        val trigger: Trigger.AdhocTrigger,
        override val invokedAt: InvokedAt,
        override val invokedBy: Requester<TenantId>
    ): InvokedTrigger()
}