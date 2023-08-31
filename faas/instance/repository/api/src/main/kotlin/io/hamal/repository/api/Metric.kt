package io.hamal.repository.api

import kotlinx.serialization.json.JsonObject
import org.jetbrains.annotations.TestOnly


enum class SystemEvent {
    ExecutionCompletedEvent, ExecInvokedEvent, ExecPlannedEvent, ExecScheduledEvent, ExecutionFailedEvent,
    ExecutionQueuedEvent, ExecutionStartedEvent, FuncCreatedEvent, FuncUpdatedEvent, NamespaceCreatedEvent,
    NamespaceUpdatedEvent, StateUpdatedEvent, TriggerCreatedEvent;
}


interface IMetrics {
    fun update(e: SystemEvent)
    fun getAsMap(): LinkedHashMap<SystemEvent, Int>
    fun getTimeStarted(): Long
    fun reset()

    @TestOnly
    fun getFailed():Int


    fun getAsJson(): JsonObject
}


