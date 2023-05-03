package io.hamal.backend.store.impl.internal

import io.hamal.backend.core.model.Trigger
import io.hamal.backend.store.api.TriggerStore
import io.hamal.lib.vo.TriggerId

object DefaultTriggerStore : TriggerStore {

    val triggers = mutableMapOf<TriggerId, Trigger>()
    override fun store(trigger: Trigger) {
        triggers[trigger.id] = trigger
    }
}