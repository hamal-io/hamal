package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.common.util.HashUtils
import io.hamal.lib.domain.vo.LogTopicId
import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.LogBrokerRepository.CreateTopicCmd
import io.hamal.repository.api.log.LogConsumerId
import io.hamal.repository.api.log.LogConsumerImpl
import io.hamal.repository.api.log.LogTopicAppenderImpl
import io.hamal.repository.fixture.AbstractIntegrationTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.TestFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class LogConsumerTest : AbstractIntegrationTest() {

    @TestFactory
    fun `Late consumer starts at the beginning`() = runWith(LogBrokerRepository::class) { testInstance ->
        val topic = testInstance.create(CreateTopicCmd(CmdId(1), LogTopicId(123)))

        val appender = LogTopicAppenderImpl<String>(testInstance)
        IntRange(1, 10).forEach { appender.append(CmdId(it), topic.id, "$it") }

        val testConsumer = LogConsumerImpl(LogConsumerId(1), topic.id, testInstance, String::class)
        testConsumer.consumeIndexed(Limit(10)) { index, _, value ->
            assertThat("${index + 1}", equalTo(value))
        }

        val counter = AtomicInteger(0)
        testConsumer.consume(Limit(10)) { _, _ ->
            CompletableFuture.runAsync {
                counter.incrementAndGet()
            }
        }

        assertThat(counter.get(), equalTo(0))

        appender.append(CmdId(1337), topic.id, "1337")
        testConsumer.consume(Limit(10)) { _, value ->
            assertThat(value, equalTo("1337"))
            counter.incrementAndGet()
        }
        assertThat(counter.get(), equalTo(1))
    }


    @TestFactory
    fun `Can run concurrent to appender`() = runWith(LogBrokerRepository::class) { testInstance ->

        val topic = testInstance.create(CreateTopicCmd(CmdId(1), LogTopicId(123)))

        val testAppender = LogTopicAppenderImpl<String>(testInstance)

        val testConsumer = LogConsumerImpl(LogConsumerId(1), topic.id, testInstance, String::class)
        val collected = mutableListOf<String>()
        val consumerFuture = CompletableFuture.runAsync {
            while (collected.size < 1_000) {
                testConsumer.consume(Limit(1)) { _, str ->
                    collected.add(str)
                }
            }
        }

        IntRange(1, 10).forEach { thread ->
            CompletableFuture.runAsync {
                IntRange(1, 100).forEach {
                    testAppender.append(CmdId(HashUtils.sha256("$thread $it")), topic.id, "$it")
                }
            }
        }

        consumerFuture.join()
        assertThat(collected, hasSize(1_000))
    }

}