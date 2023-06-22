package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.lib.common.util.HashUtils.sha256
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class BrokerIT {
    @Test
    fun `Concurrent safe - 10 threads add to the same topic`() {

        MemoryLogBrokerRepository(MemoryLogBroker(LogBroker.Id(123))).use { testInstance ->
            val topic = testInstance.resolveTopic(TopicName("topic"))

            val futures = IntRange(1, 10).map { thread ->
                CompletableFuture.runAsync {
                    IntRange(1, 1_000).forEach {
                        testInstance.append(CmdId(sha256("$thread $it")), topic, "$thread $it".toByteArray())
                    }
                }
            }

            futures.forEach { it.join() }

            val result = testInstance.consume(GroupId("group-id"), topic, 100_000)
            assertThat(result, hasSize(10_000))
        }
    }

    @Test
    fun `Concurrent safe - 100 threads add to the different topics`() {
        MemoryLogBrokerRepository(MemoryLogBroker(LogBroker.Id(123))).use { testInstance ->
            val futures = IntRange(1, 100).map { thread ->
                CompletableFuture.runAsync {
                    val topic = testInstance.resolveTopic(TopicName("topic-$thread"))
                    IntRange(1, 100).forEach {
                        testInstance.append(CmdId(sha256("$thread $it")), topic, "$thread $it".toByteArray())
                    }
                }
            }
            futures.forEach { it.join() }

            IntRange(1, 100).forEach { thread ->
                val result = testInstance.consume(
                    GroupId("group-id"),
                    testInstance.resolveTopic(TopicName("topic-$thread")),
                    1_000_000
                )
                assertThat(result, hasSize(100))
                assertThat(result.first().bytes, equalTo("$thread 1".toByteArray()))
                assertThat(result.last().bytes, equalTo("$thread 100".toByteArray()))
            }

        }
    }
}