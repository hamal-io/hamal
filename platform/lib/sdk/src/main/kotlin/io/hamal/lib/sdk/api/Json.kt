package io.hamal.lib.sdk.api

import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.common.serialization.ValueObjectHotObjectAdapter

object ApiJsonModule : JsonModule() {
    init {
        set(ApiRequested::class, ApiRequested.Adapter)
        set(ApiState::class, ValueObjectHotObjectAdapter(::ApiState))
        set(ApiTrigger::class, ApiTrigger.Adapter)
        set(ApiTriggerList.Trigger::class, ApiTriggerList.Trigger.Adapter)
    }
}