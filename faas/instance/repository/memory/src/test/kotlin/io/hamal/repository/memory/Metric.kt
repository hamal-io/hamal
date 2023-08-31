package io.hamal.repository.memory


import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.SystemEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class InMemoryMetricsTest {


    @Test
    fun mapTest() {
        val repo: MetricRepository = InMemoryMetrics()
        repo.update(SystemEvent.ExecutionCompletedEvent)
        val mp = repo.getData().getMap()
        Assertions.assertTrue(mp[SystemEvent.ExecutionCompletedEvent] == 1)
    }

    @Test
    fun resetTest() {
        val repo: MetricRepository = InMemoryMetrics()
        repo.update(SystemEvent.ExecutionCompletedEvent)
        repo.update(SystemEvent.ExecutionFailedEvent)
        repo.reset()
        val mp = repo.getData().getMap()
        Assertions.assertTrue(mp[SystemEvent.ExecutionCompletedEvent] == 0)
        Assertions.assertTrue(mp[SystemEvent.ExecutionFailedEvent] == 0)


    }

    @Test
    fun except() {
        val repo: MetricRepository = InMemoryMetrics()
        val thrown = Assertions.assertThrows(NoSuchElementException::class.java) {
            repo.update(SystemEvent.NamespaceCreatedEvent)
        }
        Assertions.assertEquals("NamespaceCreatedEvent not found", thrown.message)
    }

    @Test
    fun lockTest() {
        val repo: MetricRepository = InMemoryMetrics()
        val t1 = Thread {
            for (i in 1..50) {
                repo.update(SystemEvent.ExecutionFailedEvent)
            }
        }

        t1.start()
        val t2 = Thread {
            for (i in 1..50) {
                repo.update(SystemEvent.ExecutionFailedEvent)
            }
        }

        t2.start()
        val c = repo.getData().getMap()
        val x = c.getOrDefault(SystemEvent.ExecutionFailedEvent, 0)

        Assertions.assertTrue(x > 0)

        t1.join()
        t2.join()
        Assertions.assertEquals(100, repo.getData().getMap()[SystemEvent.ExecutionFailedEvent])
    }

}