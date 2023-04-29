package io.hamal.backend.store.api

import io.hamal.backend.core.model.Trigger

interface TriggerStore {
    fun store(trigger: Trigger)
}