package io.hamal.core.service

import io.hamal.repository.api.MetricAccess
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.repository.api.event.ExecutionFailedEvent
import io.hamal.repository.api.event.HubEvent
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class MetricService(
    private val repo: MetricRepository
) {
    private val interestingEvents: List<KClass<out HubEvent>> = listOf(
        ExecutionCompletedEvent::class,
        ExecutionFailedEvent::class
    )

    fun handleEvent(event: HubEvent) {
        if (interestingEvents.any { it.isInstance(event) }) {
            repo.update(event) {
                it.topicName.value
            }
        }
    }

    fun get(): MetricAccess {
        return repo.getData()
    }

    fun clear(){
        repo.clear()
    }
}

