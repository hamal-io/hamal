package io.hamal.repository.api

enum class SystemEvent {
    ExecutionCompletedEvent, ExecInvokedEvent, ExecPlannedEvent, ExecScheduledEvent, ExecutionFailedEvent,
    ExecutionQueuedEvent, ExecutionStartedEvent, FuncCreatedEvent, FuncUpdatedEvent, NamespaceCreatedEvent,
    NamespaceUpdatedEvent, StateUpdatedEvent, TriggerCreatedEvent;
}

interface AccessMetrics {
    fun getTime(): Long
    fun getMap(): LinkedHashMap<SystemEvent, Int>
}

interface MetricRepository {
    fun update(e: SystemEvent)
    fun getData(): AccessMetrics
    fun clear()
}


