package io.hamal.repository.memory.log

import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*


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
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.name, equalTo(TopicName("very-first-topic")))
            assertThat(testInstance.count {}, equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.create(
                CmdId(1),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            val result = testInstance.create(
                CmdId(2),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(2),
                    TopicName("func::created")
                )
            )
            assertThat(result.id, equalTo(TopicId(2)))
            assertThat(result.name, equalTo(TopicName("func::created")))

            assertThat(testInstance.count {}, equalTo(2UL))
        }

        @Test
        fun `Does not creat a new entry if topic already exists`() {
            testInstance.create(
                CmdId(1),
                BrokerTopicsRepository.TopicToCreate(
                    TopicId(1),
                    TopicName("very-first-topic")
                )
            )

            val throwable = assertThrows<IllegalArgumentException> {
                testInstance.create(
                    CmdId(2),
                    BrokerTopicsRepository.TopicToCreate(
                        TopicId(2),
                        TopicName("very-first-topic")
                    )
                )
            }
            assertThat(throwable.message, equalTo("Topic already exists"))

            assertThat(testInstance.count {}, equalTo(1UL))
        }

        private val testInstance = MemoryBrokerTopicsRepository()
    }

}