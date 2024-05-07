package io.hamal.lib.sdk.api

import io.hamal.lib.common.serialization.SerdeModuleJson
import io.hamal.lib.common.value.serde.ValueVariableAdapters

sealed class ApiObject {
    val `class`: String = this::class.java.simpleName
}

object SerdeModuleJsonApi : SerdeModuleJson() {
    init {
        set(ApiRequested::class, ApiRequested.Adapter)
        set(ApiState::class, ValueVariableAdapters.Object(::ApiState))
        set(ApiTrigger::class, ApiTrigger.Adapter)
        set(ApiTriggerList.Trigger::class, ApiTriggerList.Trigger.Adapter)
    }
}