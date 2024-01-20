package io.hamal.lib.sdk.api

import io.hamal.lib.common.serialization.JsonModule
import io.hamal.lib.common.serialization.ValueObjectHotObjectAdapter

sealed class ApiObject {
    val `class`: String = this::class.java.simpleName
}

object ApiJsonModule : JsonModule() {
    init {
        set(ApiRequested::class, ApiRequested.Adapter)
        set(ApiState::class, ValueObjectHotObjectAdapter(::ApiState))
        set(ApiTrigger::class, ApiTrigger.Adapter)
        set(ApiTriggerList.Trigger::class, ApiTriggerList.Trigger.Adapter)
        //TODO-142 set(FeedbackCreateRequest::class, FeedbackCreateRequest.Adapter)
    }
}