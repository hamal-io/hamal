package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.serde.ValueJsonAdapters.StringVariable

object SerdeModuleJsonInternalEvent : SerdeModuleJson() {
    init {
        set(InternalEventClass::class, StringVariable(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}