package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.common.value.ValueJsonAdapters

object PlatformEventJsonModule : SerializationModule() {
    init {
        set(InternalEventClass::class, ValueJsonAdapters.StringVariable(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}