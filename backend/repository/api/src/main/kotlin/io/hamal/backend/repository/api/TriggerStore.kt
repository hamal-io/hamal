package io.hamal.backend.repository.api

import io.hamal.backend.core.model.Trigger

interface TriggerStore {
    fun store(trigger: Trigger)
}