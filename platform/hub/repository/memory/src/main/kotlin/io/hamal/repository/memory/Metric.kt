package io.hamal.repository.memory


import io.hamal.repository.api.MetricData
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.HubEvent
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


class MemoryMetricRepository(
    val time: Long = System.currentTimeMillis()
) : MetricRepository {

    private val lock = ReentrantReadWriteLock()
    private var data: MetricData = MetricData(time, mutableListOf())


    private fun read(): MetricData {
        lock.read { return MetricData(data.time, data.events) }
    }

    private fun write(f: () -> Unit) {
        lock.write {
            f()
        }
    }

    override fun update(e: HubEvent, transform: (HubEvent) -> String) {
        write {
            val friendlyName = transform(e)
            val match: MetricData.Count? = data.events.find { it.name == friendlyName }
            if (match == null) {
                data.events.add(MetricData.Count(friendlyName, 1))
            } else {
                match.value++
            }
        }
    }

    override fun get(): MetricData {
        return read()
    }

    override fun clear() {
        data = MetricData()
    }
}


val memoryMetricRepository = MemoryMetricRepository()
