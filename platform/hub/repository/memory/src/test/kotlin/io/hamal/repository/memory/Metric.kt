package io.hamal.repository.memory


import io.hamal.repository.api.MetricAccess
import io.hamal.repository.api.MetricRepository
import io.hamal.repository.api.event.HubEvent
import io.hamal.repository.api.event.HubEventTopic
import io.hamal.repository.api.event.topicName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass


class MemoryMetricTest {


    // val repo: MetricRepository = MemoryMetricRepository


    /*   @BeforeEach
       fun clearRepo() {
           repo.clear()
       }*/

    @Test
    fun mapTest() {
        val subs = HubEvent::class.sealedSubclasses
        println("")


    }

    /*
   @Test
    fun resetTest() {
        repo.update(ExecutionCompletedEvent)
        repo.update(ExecutionFailedEvent)
        repo.clear()
        val mp = repo.getData().getMap()
        Assertions.assertTrue(mp[SystemEvent.ExecutionCompletedEvent] == 0)
        Assertions.assertTrue(mp[SystemEvent.ExecutionFailedEvent] == 0)
    }

    @Test
    fun lockTest() {
        val t1 = Thread {
            for (i in 1..50) {
                repo.update(HubEvent)
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
    }*/

}