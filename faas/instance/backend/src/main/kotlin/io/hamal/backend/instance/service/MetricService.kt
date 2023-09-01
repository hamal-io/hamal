package io.hamal.backend.instance.service

import io.hamal.backend.instance.event.events.*
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.SystemEvent
import org.springframework.stereotype.Service

@Service
class MetricService(private val repo: MetricRepository) {

    fun handleEvent(event: InstanceEvent) {
        when (event) {
            is ExecutionCompletedEvent -> repo.update(SystemEvent.ExecutionCompletedEvent)
            is ExecPlannedEvent -> repo.update(SystemEvent.ExecPlannedEvent)
            is ExecScheduledEvent -> repo.update(SystemEvent.ExecScheduledEvent)
            is ExecutionFailedEvent -> repo.update(SystemEvent.ExecutionFailedEvent)
            is ExecutionQueuedEvent -> repo.update(SystemEvent.ExecutionQueuedEvent)
            is ExecutionStartedEvent -> repo.update(SystemEvent.ExecutionStartedEvent)
            is FuncCreatedEvent -> repo.update(SystemEvent.FuncCreatedEvent)
            is FuncUpdatedEvent -> repo.update(SystemEvent.FuncUpdatedEvent)
            is NamespaceCreatedEvent -> repo.update(SystemEvent.NamespaceCreatedEvent)
            is NamespaceUpdatedEvent -> repo.update(SystemEvent.NamespaceUpdatedEvent)
            is StateUpdatedEvent -> repo.update(SystemEvent.StateUpdatedEvent)
            is TriggerCreatedEvent -> repo.update(SystemEvent.TriggerCreatedEvent)
        }
    }
}
