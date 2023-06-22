package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogBroker
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class MemoryLogBrokerTopicsRepositoryTest {
    @Nested
    inner class ResolveTopicTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Creates a new entry if topic does not exists`() {
            val result = testInstance.resolveTopic(TopicName("very-first-topic"))
            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(345)))
            assertThat(result.name, equalTo(TopicName("very-first-topic")))

            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.resolveTopic(TopicName("very-first-topic"))

            val result = testInstance.resolveTopic(TopicName("func::created"))
            assertThat(result.id, equalTo(TopicId(2)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(345)))
            assertThat(result.name, equalTo(TopicName("func::created")))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        @Test
        fun `Does not creat a new entry if topic already exists`() {
            testInstance.resolveTopic(TopicName("yet-another-topic"))
            testInstance.resolveTopic(TopicName("another-topic"))

            testInstance.resolveTopic(TopicName("some-topic"))

            testInstance.resolveTopic(TopicName("some-more-topic"))
            testInstance.resolveTopic(TopicName("some-more-mor-topic"))


            val result = testInstance.resolveTopic(TopicName("some-topic"))
            assertThat(result.id, equalTo(TopicId(3)))
            assertThat(result.logBrokerId, equalTo(LogBroker.Id(345)))
            assertThat(result.name, equalTo(TopicName("some-topic")))

            assertThat(testInstance.count(), equalTo(5UL))
        }

        private val testInstance = MemoryLogBrokerTopicsRepository(
            MemoryBrokerTopics(
                logBrokerId = LogBroker.Id(345),
            )
        )
    }

}