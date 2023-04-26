package io.hamal.lib.log.broker

import io.hamal.lib.log.consumer.Consumer.GroupId
import io.hamal.lib.log.topic.Topic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

class BrokerIT {

    @Test
    fun `Concurrent safe - 10 threads add to the same topic`() {
        val path = Files.createTempDirectory("broker_it")

        BrokerRepository.open(Broker(Broker.Id(123), path)).use { testInstance ->
            val topic = testInstance.resolveTopic(Topic.Name("topic"))

            val futures = IntRange(1, 10).map { thread ->
                CompletableFuture.runAsync {
                    IntRange(1, 1_000).forEach {
                        testInstance.append(topic, "$thread $it".toByteArray())
                    }
                }
            }

            futures.forEach { it.join() }

            val result = testInstance.read(GroupId("group-id"), topic, 100_000)
            assertThat(result, hasSize(10_000))
        }
    }

    @Test
    fun `Concurrent safe - 100 threads add to the different topics`() {
        val path = Files.createTempDirectory("broker_it")

        BrokerRepository.open(Broker(Broker.Id(123), path)).use { testInstance ->
            val futures = IntRange(1, 100).map { thread ->
                CompletableFuture.runAsync {
                    val topic = testInstance.resolveTopic(Topic.Name("topic-$thread"))
                    IntRange(1, 100).forEach {
                        testInstance.append(topic, "$thread $it".toByteArray())
                    }
                }
            }

            futures.forEach { it.join() }

            IntRange(1, 100).forEach { thread ->
                val result = testInstance.read(
                    GroupId("group-id"),
                    testInstance.resolveTopic(Topic.Name("topic-$thread")),
                    1_000_000
                )

                assertThat(result, hasSize(100))
                assertThat(result.first().bytes, equalTo("$thread 1".toByteArray()))
                assertThat(result.last().bytes, equalTo("$thread 100".toByteArray()))
            }

        }
    }
}