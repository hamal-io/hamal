package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.common.serialization.ValueObjectStringAdapter

object PlatformEventJsonModule : JsonModule() {
    init {
        set(PlatformEventClass::class, ValueObjectStringAdapter(::PlatformEventClass))
        set(PlatformEvent::class, PlatformEvent.Adapter)
    }
}