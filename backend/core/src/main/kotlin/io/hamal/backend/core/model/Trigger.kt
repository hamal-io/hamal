package io.hamal.backend.core.model

import io.hamal.backend.core.model.Trigger.ManualTrigger
import io.hamal.backend.core.model.Trigger.Type.Manual
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.vo.*

sealed class Trigger(
    val type: Type,
    val id: TriggerId,
    val reference: TriggerReference,
    val jobDefinitionId: JobDefinitionId
) : DomainObject {

    enum class Type {
        Manual
    }

    class ManualTrigger(
        id: TriggerId,
        reference: TriggerReference,
        jobDefinitionId: JobDefinitionId
    ) : Trigger(Manual, id, reference, jobDefinitionId)

}

sealed class InvokedTrigger<TRIGGER : Trigger>(
    val id: InvokedTriggerId,
    val trigger: TRIGGER,
    val invokedAt: InvokedAt,
    val invokedBy: AccountId
) : DomainObject {

    class Manual(
        id: InvokedTriggerId,
        trigger: ManualTrigger,
        invokedAt: InvokedAt,
        invokedBy: AccountId
    ) : InvokedTrigger<ManualTrigger>(id, trigger, invokedAt, invokedBy)

}