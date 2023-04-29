package io.hamal.backend.core.model

import io.hamal.backend.core.model.Trigger.ManualTrigger
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.*
import kotlinx.serialization.Serializable

@Serializable
sealed class Trigger : DomainObject {

    abstract val id: TriggerId
    abstract val reference: TriggerReference
    abstract val flowDefinitionId: FlowDefinitionId

    @Serializable
    data class ManualTrigger(
        override val id: TriggerId,
        override val reference: TriggerReference,
        override val flowDefinitionId: FlowDefinitionId
    ) : Trigger()

}

@Serializable
sealed class InvokedTrigger : DomainObject {
    abstract val id: InvokedTriggerId
    abstract val invokedAt: InvokedAt
    abstract val invokedBy: AccountId

    @Serializable
    data class Manual(
        override val id: InvokedTriggerId,
        val trigger: ManualTrigger,
        override val invokedAt: InvokedAt,
        override val invokedBy: AccountId
    ) : InvokedTrigger()

}