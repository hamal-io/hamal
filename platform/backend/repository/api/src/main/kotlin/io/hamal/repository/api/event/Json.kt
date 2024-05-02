package io.hamal.repository.api.event

import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.common.serialization.JsonAdapters

object PlatformEventJsonModule : HotModule() {
    init {
        set(InternalEventClass::class, JsonAdapters.StringVariable(::InternalEventClass))
        set(InternalEvent::class, InternalEvent.Adapter)
    }
}