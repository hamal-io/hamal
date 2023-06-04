package io.hamal.backend.repository.sqlite.log

import io.hamal.backend.repository.api.log.GroupId
import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.lib.common.util.HashUtils.sha256
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class ConsumerIT {
    @Nested
    inner class ProtobufLogConsumerTest {
        @Test
        fun `Late consumer starts at the beginning`() {
            val path = Files.createTempDirectory("broker_it")

            DefaultLogBrokerRepository(LogBroker(LogBroker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(TopicName("topic"))
                val appender = ProtobufAppender(String::class, brokerRepository)
                IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }

                val testInstance = ProtobufLogConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                testInstance.consumeIndexed(10) { index, _, value ->
                    CompletableFuture.runAsync {
                        assertThat("${index + 1}", equalTo(value))
                    }
                }

                val counter = AtomicInteger(0)
                testInstance.consume(10) { _, _ ->
                    CompletableFuture.runAsync {
                        counter.incrementAndGet()
                    }
                }

                assertThat(counter.get(), equalTo(0))

                appender.append(CmdId(1337), topic, "1337")
                testInstance.consume(10) { _, value ->
                    CompletableFuture.runAsync {
                        assertThat(value, equalTo("1337"))
                        counter.incrementAndGet()
                    }
                }
                assertThat(counter.get(), equalTo(1))
            }
        }

        @Test
        fun `Best effort to consume chunk once`() {
            val path = Files.createTempDirectory("broker_it")

            DefaultLogBrokerRepository(LogBroker(LogBroker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(TopicName("topic"))
                val appender = ProtobufAppender(String::class, brokerRepository)
                IntRange(1, 10).forEach { appender.append(CmdId(it), topic, "$it") }
            }

            DefaultLogBrokerRepository(LogBroker(LogBroker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(TopicName("topic"))
                val testInstance = ProtobufLogConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                testInstance.consumeIndexed(10) { index, _, value ->
                    CompletableFuture.runAsync {
                        assertThat("${index + 1}", equalTo(value))
                    }
                }
            }
        }

        @Test
        fun `Can run concurrent to appender`() {
            val path = Files.createTempDirectory("broker_it")

            DefaultLogBrokerRepository(LogBroker(LogBroker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(TopicName("topic"))
                val appender = ProtobufAppender(String::class, brokerRepository)

                val testInstance = ProtobufLogConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                val collected = mutableListOf<String>()
                val consumerFuture = CompletableFuture.runAsync {
                    while (collected.size < 1_000) {
                        testInstance.consume(1) { _, str ->
                            CompletableFuture.runAsync {
                                collected.add(str)
                            }
                        }
                    }
                }

                IntRange(1, 10).forEach { thread ->
                    CompletableFuture.runAsync {
                        IntRange(1, 100).forEach {
                            appender.append(CmdId(sha256("$thread $it")), topic, "$it")
                        }
                    }
                }

                consumerFuture.join()
                assertThat(collected, hasSize(1_000))
            }
        }
    }
}