package io.hamal.repository.api

import io.hamal.lib.common.serialization.HotModule
import io.hamal.lib.domain.request.Requested

object DomainJsonModule : HotModule() {
    init {
        this[Exec::class] = Exec.Adapter
        this[Requested::class] = Requested.Adapter
        this[Topic::class] = Topic.Adapter
        this[Trigger::class] = Trigger.Adapter
    }
}