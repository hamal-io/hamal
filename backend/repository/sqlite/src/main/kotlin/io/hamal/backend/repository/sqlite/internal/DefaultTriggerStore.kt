package io.hamal.backend.repository.sqlite.internal

import io.hamal.backend.core.trigger.Trigger
import io.hamal.lib.vo.TriggerId

object DefaultTriggerStore : io.hamal.backend.repository.api.Trigger {

    val triggers = mutableMapOf<TriggerId, Trigger>()
    override fun store(trigger: Trigger) {
        triggers[trigger.id] = trigger
    }
}