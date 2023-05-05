package io.hamal.backend.repository.api

import io.hamal.backend.core.trigger.Trigger

interface Trigger {
    fun store(trigger: Trigger)
}