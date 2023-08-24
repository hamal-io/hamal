package io.hamal.faas.instance.backend.repository.log

import io.hamal.backend.repository.api.log.*
import io.hamal.faas.instance.backend.repository.AbstractIntegrationTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.HashUtils
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class ConsumerTest : AbstractIntegrationTest() {
    @TestFactory
    fun `Late consumer starts at the beginning`() = runWith(LogBrokerRepository::class) { testInstance ->

        val topic = testInstance.create(
            CmdId(1),
            CreateTopic.TopicToCreate(TopicId(123), TopicName("topic"))
        )

        val appender = ProtobufAppender(String::class, testInstance)
        IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }

        val testConsumer = ProtobufLogConsumer(GroupId("consumer-01"), topic, testInstance, String::class)
        testConsumer.consumeIndexed(10) { index, _, value ->
            assertThat("${index + 1}", equalTo(value))
        }

        val counter = AtomicInteger(0)
        testConsumer.consume(10) { _, _ ->
            CompletableFuture.runAsync {
                counter.incrementAndGet()
            }
        }

        assertThat(counter.get(), equalTo(0))

        appender.append(CmdId(1337), topic, "1337")
        testConsumer.consume(10) { _, value ->
            assertThat(value, equalTo("1337"))
            counter.incrementAndGet()
        }
        assertThat(counter.get(), equalTo(1))
    }


    @TestFactory
    fun `Can run concurrent to appender`() = runWith(LogBrokerRepository::class) { testInstance ->

        val topic = testInstance.create(
            CmdId(1),
            CreateTopic.TopicToCreate(TopicId(123), TopicName("topic"))
        )

        val testAppender = ProtobufAppender(String::class, testInstance)

        val testConsumer = ProtobufLogConsumer(GroupId("consumer-01"), topic, testInstance, String::class)
        val collected = mutableListOf<String>()
        val consumerFuture = CompletableFuture.runAsync {
            while (collected.size < 1_000) {
                testConsumer.consume(1) { _, str ->
                    collected.add(str)
                }
            }
        }

        IntRange(1, 10).forEach { thread ->
            CompletableFuture.runAsync {
                IntRange(1, 100).forEach {
                    testAppender.append(CmdId(HashUtils.sha256("$thread $it")), topic, "$it")
                }
            }
        }

        consumerFuture.join()
        assertThat(collected, Matchers.hasSize(1_000))
    }

}