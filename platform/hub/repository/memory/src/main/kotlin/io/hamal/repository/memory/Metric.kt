package io.hamal.repository.memory


import io.hamal.repository.api.MetricAccess
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.HubEvent
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

    override fun update(e: HubEvent, transform: (HubEvent) -> String) {
        lock.write {
            val friendlyName = transform(e)
            if (!eventMap.containsKey(friendlyName)) {
                eventMap.put(friendlyName, 0)
            }
            eventMap[friendlyName] = eventMap.getOrDefault(friendlyName, 0) + 1
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

