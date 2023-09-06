package io.hamal.repository.memory


import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MemoryMetricTest {


    @Nested
    inner class APITest {
        val repo: MetricRepository = MemoryMetricRepository
        val hubEvents = HubEvent::class.sealedSubclasses.map { it.topicName().value }

        @BeforeEach
        fun clearRepo() {
            repo.clear()
        }

        @Test
        fun hubEvent() {
            for (i in hubEvents) {
                repo.update(i)
            }
            Assertions.assertEquals(hubEvents.size, repo.getData().getMap().size)
        }

        @Test
        fun increment() {
            val str = hubEvents.get(0)
            repo.update(str)
            Assertions.assertTrue(repo.getData().getMap()[hubEvents.get(0)] == 1)
        }

        @Test
        fun multithreadTest() {
            val executor = Executors.newFixedThreadPool(4)

            val tasks = List(100) {
                Runnable {
                    for (i in hubEvents) {
                        repo.update(i)
                    }
                }
            }

            tasks.forEach { executor.submit(it) }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.MINUTES)

            for (i in repo.getData().getMap()) {
                //println(i.value)
                Assertions.assertEquals(99, i.value)
            }
        }


    }


}