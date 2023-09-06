package io.hamal.core.service

import io.hamal.repository.api.MetricAccess
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.*
import org.springframework.stereotype.Service

@Service
class MetricService(
    private val repo: MetricRepository
) {
    fun handleEvent(event: HubEvent) {
        repo.update(event)
    }

    fun get(): MetricAccess {
        return repo.getData()
    }
}

