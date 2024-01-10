package io.hamal.lib.sdk.api

import io.hamal.lib.common.serialization.JsonModule

object ApiJsonModule : JsonModule() {
    init {
        set(ApiRequested::class, ApiRequested.Adapter)
        set(ApiTrigger::class, ApiTrigger.Adapter)
        set(ApiTriggerList.Trigger::class, ApiTriggerList.Trigger.Adapter)
    }
}