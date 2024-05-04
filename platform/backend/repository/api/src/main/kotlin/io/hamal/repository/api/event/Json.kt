package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.serde.ValueVariableAdapters

object SerdeModuleJsonInternalEvent : SerdeModuleJson() {
    init {
        set(InternalEventClass::class, ValueVariableAdapters.String(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}