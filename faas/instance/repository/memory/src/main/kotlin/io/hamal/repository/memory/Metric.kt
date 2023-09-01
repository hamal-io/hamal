package io.hamal.repository.memory


import io.hamal.repository.api.AccessMetrics
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.SystemEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.util.concurrent.locks.ReentrantReadWriteLock


object MemoryMetricRepository : MetricRepository {

    private val lock = ReentrantReadWriteLock()
    private val timer: Long = System.currentTimeMillis()


    @Serializable
    private val eventMap: LinkedHashMap<SystemEvent, Int> = linkedMapOf(
        SystemEvent.ExecutionCompletedEvent to 0,
        SystemEvent.ExecutionFailedEvent to 0,
        SystemEvent.ExecPlannedEvent to 0,
        SystemEvent.ExecutionQueuedEvent to 0,
        SystemEvent.ExecutionStartedEvent to 0,
        SystemEvent.ExecScheduledEvent to 0,
        SystemEvent.ExecInvokedEvent to 0
    )

    private fun read(): LinkedHashMap<SystemEvent, Int> {
        val readLock = lock.readLock()
        readLock.lock()
        try {
            return LinkedHashMap(eventMap)
        } finally {
            readLock.unlock()
        }
    }

    override fun update(key: SystemEvent) {
        val writeLock = lock.writeLock()
        writeLock.lock()
        try {
            if (!eventMap.containsKey(key)) throw NoSuchElementException("$key not found")
            eventMap[key] = eventMap.getOrDefault(key, 0) + 1
        } finally {
            writeLock.unlock()
        }
    }

    override fun getData(): AccessMetrics {
        return object : AccessMetrics {
            override fun getTime(): Long {
                return timer
            }

            override fun getMap(): LinkedHashMap<SystemEvent, Int> {
                return LinkedHashMap(eventMap.toMap())
            }
        }
    }

    fun getAsJson(): JsonObject {
        //TODO for use in other class
        val data = read()
        val arr = buildJsonArray {
            for (i in data) {
                addJsonObject {
                    put(i.key.name, i.value)
                }
            }
        }

        return buildJsonObject {
            put("timer", timer)
            put("events", arr)
        }
    }


    override fun clear() {
        val writeLock = lock.writeLock()
        writeLock.lock()
        try {
            for (i in eventMap) {
                i.setValue(0)
            }
        } finally {
            writeLock.unlock()
        }
    }
}