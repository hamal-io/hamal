package io.hamal.backend.repository.api

import io.hamal.backend.core.model.Trigger

interface Trigger {
    fun store(trigger: Trigger)
}