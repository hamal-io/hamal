package io.hamal.repository.log

import io.hamal.backend.repository.api.log.CreateTopic.TopicToCreate
import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.repository.AbstractIntegrationTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.HashUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.TestFactory
import java.util.concurrent.CompletableFuture.runAsync

class BrokerTest : AbstractIntegrationTest() {

    @TestFactory
    fun `Concurrent safe - 10 threads add to the same topic`() = runWith(LogBrokerRepository::class) { testInstance ->
        val topic = testInstance.create(
            CmdId(1), TopicToCreate(
                TopicId(123), TopicName("topic")
            )
        )

        val futures = IntRange(1, 10).map { thread ->
            runAsync {
                IntRange(1, 1_000).forEach {
                    testInstance.append(
                        CmdId(HashUtils.sha256("$thread $it")),
                        topic,
                        "$thread $it".toByteArray()
                    )
                }
            }
        }

        futures.forEach { it.join() }

        val result = testInstance.consume(GroupId("group-id"), topic, 100_000)
        assertThat(result, hasSize(10_000))
    }

    @TestFactory
    fun `Concurrent safe - 100 threads add to the different topics`() =
        runWith(LogBrokerRepository::class) { testInstance ->

            val futures = IntRange(1, 100).map { thread ->
                runAsync {
                    val topic = testInstance.create(
                        CmdId(1),
                        TopicToCreate(TopicId(thread), TopicName("topic-$thread"))
                    )

                    IntRange(1, 100).forEach {
                        testInstance.append(
                            CmdId(HashUtils.sha256("$thread $it")),
                            topic,
                            "$thread $it".toByteArray()
                        )
                    }
                }
            }
            futures.forEach { it.join() }

            IntRange(1, 100).forEach { thread ->
                val result = testInstance.consume(
                    GroupId("group-id"),
                    testInstance.findTopic(TopicName("topic-$thread"))!!,
                    1_000_000
                )
                assertThat(result, hasSize(100))
                assertThat(result.first().bytes, equalTo("$thread 1".toByteArray()))
                assertThat(result.last().bytes, equalTo("$thread 100".toByteArray()))
            }

        }
}