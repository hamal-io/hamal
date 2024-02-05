package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.common.serialization.ValueObjectStringAdapter

object PlatformEventJsonModule : JsonModule() {
    init {
        set(InternalEventClass::class, ValueObjectStringAdapter(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}