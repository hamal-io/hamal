package io.hamal.backend.repository.memory.log

import io.hamal.backend.repository.api.log.LogBrokerTopicsRepository
import io.hamal.lib.domain.CmdId
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
    inner class CreateTopicTest {
        @BeforeEach
        fun setup() {
            testInstance.clear()
        }

        @AfterEach
        fun after() {
            testInstance.close()
        }

        @Test
        fun `Creates a new topic if topic does not exists`() {
            val result = testInstance.create(
                CmdId(1),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.name, equalTo(TopicName("very-first-topic")))
            assertThat(testInstance.count(), equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.create(
                CmdId(1),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            val result = testInstance.create(
                CmdId(2),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(2),
                    TopicName("func::created")
                )
            )
            assertThat(result.id, equalTo(TopicId(2)))
            assertThat(result.name, equalTo(TopicName("func::created")))

            assertThat(testInstance.count(), equalTo(2UL))
        }

        @Test
        fun `Does not creat a new entry if topic already exists`() {
            testInstance.create(
                CmdId(1),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            testInstance.create(
                CmdId(2),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(2),
                    TopicName("very-first-topic")
                )
            )

            testInstance.create(
                CmdId(3),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(3),
                    TopicName("very-first-topic")
                )
            )
            testInstance.create(
                CmdId(4),
                LogBrokerTopicsRepository.TopicToCreate(
                    TopicId(4),
                    TopicName("very-first-topic")
                )
            )

            assertThat(testInstance.count(), equalTo(1UL))
        }

        private val testInstance = MemoryLogBrokerTopicsRepository()
    }

}