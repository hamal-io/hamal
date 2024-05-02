package io.hamal.lib.sdk.api

import io.hamal.lib.common.serialization.SerializationModule
import io.hamal.lib.common.value.ValueJsonAdapters
import io.hamal.lib.domain.request.FeedbackCreateRequest

sealed class ApiObject {
    val `class`: String = this::class.java.simpleName
}

object ApiJsonModule : SerializationModule() {
    init {
        set(ApiRequested::class, ApiRequested.Adapter)
        set(ApiState::class, ValueJsonAdapters.ObjectVariable(::ApiState))
        set(ApiTrigger::class, ApiTrigger.Adapter)
        set(ApiTriggerList.Trigger::class, ApiTriggerList.Trigger.Adapter)
        set(FeedbackCreateRequest::class, FeedbackCreateRequest.Adapter)
    }
}