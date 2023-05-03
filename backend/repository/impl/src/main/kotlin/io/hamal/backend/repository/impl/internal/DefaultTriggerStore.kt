package io.hamal.backend.repository.impl.internal

import io.hamal.backend.core.model.Trigger
import io.hamal.backend.repository.api.TriggerStore
import io.hamal.lib.vo.TriggerId

object DefaultTriggerStore : TriggerStore {

    val triggers = mutableMapOf<TriggerId, Trigger>()
    override fun store(trigger: Trigger) {
        triggers[trigger.id] = trigger
    }
}