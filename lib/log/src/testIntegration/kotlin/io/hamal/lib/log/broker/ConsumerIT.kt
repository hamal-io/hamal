package io.hamal.lib.log.broker

import io.hamal.lib.log.appender.ProtobufAppender
import io.hamal.lib.log.consumer.DepConsumer.GroupId
import io.hamal.lib.log.consumer.ProtobufConsumer
import io.hamal.lib.log.topic.Topic
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class ConsumerIT {
    @Nested
    @DisplayName("ProtobufConsumer")
    inner class ProtobufConsumerTest {
        @Test
        fun `Late consumer starts at the beginning`() {
            val path = Files.createTempDirectory("broker_it")

            BrokerRepository.open(Broker(Broker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(Topic.Name("topic"))
                val appender = ProtobufAppender(String::class, brokerRepository)
                IntRange(1, 10).forEach { appender.append(topic, "$it") }

                val testInstance = ProtobufConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                testInstance.consumeIndexed(10) { index, value ->
                    CompletableFuture.runAsync {
                        assertThat("${index + 1}", equalTo(value))
                    }
                }

                val counter = AtomicInteger(0)
                testInstance.consume(10) { _ ->
                    CompletableFuture.runAsync {
                        counter.incrementAndGet()
                    }
                }

                assertThat(counter.get(), equalTo(0))

                appender.append(topic, "1337")
                testInstance.consume(10) { value ->
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

            BrokerRepository.open(Broker(Broker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(Topic.Name("topic"))
                val appender = ProtobufAppender(String::class, brokerRepository)
                IntRange(1, 10).forEach { appender.append(topic, "$it") }
            }

            BrokerRepository.open(Broker(Broker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(Topic.Name("topic"))
                val testInstance = ProtobufConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                testInstance.consumeIndexed(10) { index, value ->
                    CompletableFuture.runAsync {
                        assertThat("${index + 1}", equalTo(value))
                    }
                }
            }
        }

        @Test
        fun `Can run concurrent to appender`() {
            val path = Files.createTempDirectory("broker_it")

            BrokerRepository.open(Broker(Broker.Id(123), path)).use { brokerRepository ->
                val topic = brokerRepository.resolveTopic(Topic.Name("topic"))
                val appender = ProtobufAppender(String::class, brokerRepository)

                val testInstance = ProtobufConsumer(GroupId("consumer-01"), topic, brokerRepository, String::class)
                val collected = mutableListOf<String>()
                val consumerFuture = CompletableFuture.runAsync {
                    while (collected.size < 1_000) {
                        testInstance.consume(1) {
                            CompletableFuture.runAsync {
                                collected.add(it)
                            }
                        }
                    }
                }

                IntRange(1, 10).forEach { _ ->
                    CompletableFuture.runAsync {
                        IntRange(1, 100).forEach {
                            appender.append(topic, "$it")
                        }
                    }
                }

                consumerFuture.join()
                assertThat(collected, hasSize(1_000))
            }
        }
    }
}