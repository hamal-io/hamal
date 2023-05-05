package io.hamal.backend.repository.impl.internal

import io.hamal.lib.vo.TriggerId

object DefaultTriggerStore : io.hamal.backend.repository.api.Trigger {

    val triggers = mutableMapOf<TriggerId, io.hamal.backend.core.model.Trigger>()
    override fun store(trigger: io.hamal.backend.core.model.Trigger) {
        triggers[trigger.id] = trigger
    }
}