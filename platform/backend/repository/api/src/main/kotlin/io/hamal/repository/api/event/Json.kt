package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.ValueJsonAdapters.StringVariable

object InternalEventJsonModule : SerdeModuleJson() {
    init {
        set(InternalEventClass::class, StringVariable(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}