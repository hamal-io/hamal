package io.hamal.repository.memory


import io.hamal.repository.api.IMetrics
import io.hamal.repository.api.SystemEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.locks.ReentrantReadWriteLock


class InMemoryMetrics : IMetrics {

    private class MapHolder {
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

        fun read(): LinkedHashMap<SystemEvent, Int> {
            val readLock = lock.readLock()
            readLock.lock()
            try {
                return LinkedHashMap(eventMap)
            } finally {
                readLock.unlock()
            }
        }

        fun increment(key: SystemEvent) {
            val writeLock = lock.writeLock()
            writeLock.lock()
            try {
                if (!eventMap.containsKey(key)) throw NoSuchElementException("${key} not found")
                eventMap[key] = eventMap.getOrDefault(key, 0) + 1
            } finally {
                writeLock.unlock()
            }
        }

        fun getTime(): Long {
            return timer
        }

        @TestOnly
        fun getFailed(): Int {
            return read().getOrDefault(SystemEvent.ExecutionFailedEvent, -1)
        }

        fun countersReset() {
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

    private val mapHolder = MapHolder()


    override fun update(e: SystemEvent) {
        mapHolder.increment(e)
    }

    override fun getTimeStarted(): Long {
        return mapHolder.getTime()
    }

    override fun getAsMap(): LinkedHashMap<SystemEvent, Int> {
        return mapHolder.read()
    }


    override fun getAsJson(): JsonObject {
        val data = getAsMap()
        /*
        var c = 0;
         val str = StringBuilder("{\"timer\":${getTimeStarted()},\"events\":[")
         for (i in data.entries) {
             str.append("{\" ${i.key.name} \": ${i.value}}")
             if (c < data.count() - 1) {
                 str.append(",") // last "," invalid
             }
             c++
         }
         str.append("]}")
         return Json.parseToJsonElement(str.toString()).jsonObject
         */
        val arr = buildJsonArray {
            for (i in data) {
                addJsonObject {
                    put(i.key.name, i.value)
                }
            }
        }

        return buildJsonObject {
            put("timer", getTimeStarted())
            put("events", arr)
        }


    }


    @TestOnly
    override fun getFailed(): Int {
        return mapHolder.getFailed()
    }


    override fun reset() {
        mapHolder.countersReset()
    }
}