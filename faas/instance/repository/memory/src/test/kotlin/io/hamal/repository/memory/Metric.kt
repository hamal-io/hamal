package io.hamal.repository.memory


import io.hamal.repository.api.IMetrics
import io.hamal.repository.api.SystemEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class InMemoryMetricsTest {

    @Nested
    inner class ApiTest {
        @Test
        fun failedTest() {
            val repo: IMetrics = InMemoryMetrics()
            repo.update(SystemEvent.ExecutionFailedEvent)
            Assertions.assertTrue(repo.getFailed() == 1)
        }

        @Test
        fun mapTest() {
            val repo: IMetrics = InMemoryMetrics()
            repo.update(SystemEvent.ExecutionCompletedEvent)
            val mp = repo.getAsMap()
            Assertions.assertTrue(mp[SystemEvent.ExecutionCompletedEvent] == 1)
        }

        @Test
        fun resetTest() {
            val repo: IMetrics = InMemoryMetrics()
            repo.update(SystemEvent.ExecutionCompletedEvent)
            repo.update(SystemEvent.ExecutionFailedEvent)
            repo.reset()
            val mp = repo.getAsMap()
            Assertions.assertTrue(mp[SystemEvent.ExecutionCompletedEvent] == 0)
            Assertions.assertTrue(repo.getFailed() == 0)
        }
    }


    @Test
    fun except() {
        val repo: IMetrics = InMemoryMetrics()
        val thrown = Assertions.assertThrows(NoSuchElementException::class.java) {
            repo.update(SystemEvent.NamespaceCreatedEvent)
        }
        Assertions.assertEquals("NamespaceCreatedEvent not found", thrown.message)
    }

    @Test
    fun lockTest() {
        val repo: IMetrics = InMemoryMetrics()
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
        val c = repo.getAsMap()
        val x = c.getOrDefault(SystemEvent.ExecutionFailedEvent, 0)

        Assertions.assertTrue(x > 0)

        t1.join()
        t2.join()
        Assertions.assertEquals(100, repo.getFailed())
    }

}