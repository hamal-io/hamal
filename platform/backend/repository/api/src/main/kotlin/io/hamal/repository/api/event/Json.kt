package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.ValueObjectStringAdapter

object PlatformEventJsonModule : HotModule() {
    init {
        set(InternalEventClass::class, ValueObjectStringAdapter(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}