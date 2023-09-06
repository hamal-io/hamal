package io.hamal.repository.memory


import io.hamal.repository.api.MetricAccess
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.HubEvent
import io.hamal.repository.api.event.HubEventTopic
import kotlinx.serialization.json.*
import org.jetbrains.annotations.TestOnly
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


object MemoryMetricRepository : MetricRepository {

    private val lock = ReentrantReadWriteLock()
    private val eventMap = LinkedHashMap<String, Int>()
    private var timer: Long = System.currentTimeMillis()


    override fun create() {
        TODO()
    }


    private fun read(): LinkedHashMap<String, Int> {
        lock.read { return LinkedHashMap(eventMap) }
    }

    override fun update(e: HubEvent) {
        lock.write {
            val topic = e.topicName.toString()
            if (!eventMap.containsKey(topic)) {
                eventMap.put(topic, 0)
            }
            eventMap[topic] = eventMap.getOrDefault(topic, 0) + 1
        }
    }

    @TestOnly
    override fun update(e: String) {
        lock.write {
            if (!eventMap.containsKey(e)) {
                eventMap.put(e, 0)
            }
            eventMap[e] = eventMap.getOrDefault(e, 0) + 1
        }
    }

    override fun getData(): MetricAccess {
        return object : MetricAccess {
            override fun getTime(): Long {
                return timer
            }

            override fun getMap(): LinkedHashMap<String, Int> {
                return read()
            }
        }
    }

    fun getAsJson(): JsonObject {
        //TODO for use in other class
        val data = getData().getMap()
        val arr = buildJsonArray {
            for (i in data) {
                addJsonObject {
                    put(i.key, i.value)
                }
            }
        }

        return buildJsonObject {
            put("timer", timer)
            put("events", arr)
        }
    }

    override fun setTimer(timer: Long) {
        this.timer = timer
    }


    override fun clear() {
        lock.write {
            timer = System.currentTimeMillis()
            eventMap.clear()
        }
    }
}

