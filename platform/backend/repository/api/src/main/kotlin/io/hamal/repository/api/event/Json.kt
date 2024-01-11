package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.common.serialization.ValueObjectStringAdapter

object PlatformEventJsonModule : JsonModule() {
    init {
        set(PlatformEventType::class, ValueObjectStringAdapter(::PlatformEventType))
        set(PlatformEvent::class, PlatformEvent.Adapter)
    }
}