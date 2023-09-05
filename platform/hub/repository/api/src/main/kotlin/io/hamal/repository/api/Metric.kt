package io.hamal.repository.api

import io.hamal.repository.api.event.HubEvent
import io.hamal.repository.api.event.HubEventTopic

/*enum class SystemEvent {
    ExecutionCompletedEvent, ExecInvokedEvent, ExecPlannedEvent, ExecScheduledEvent, ExecutionFailedEvent,
    ExecutionQueuedEvent, ExecutionStartedEvent, FuncCreatedEvent, FuncUpdatedEvent, NamespaceCreatedEvent,
    NamespaceUpdatedEvent, StateUpdatedEvent, TriggerCreatedEvent;
}*/

interface MetricAccess {
    fun getTime(): Long
    fun getMap(): LinkedHashMap<String, Int>
}

interface MetricRepository {
    fun create()
    fun update(e: HubEvent)
    fun update(e: HubEventTopic)
    fun getData(): MetricAccess
    fun clear()
    fun setTimer(timer: Long)
}


