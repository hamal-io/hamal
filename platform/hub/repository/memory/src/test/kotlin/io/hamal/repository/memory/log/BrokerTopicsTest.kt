package io.hamal.repository.memory.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.*


class MemoryBrokerTopicsRepositoryTest {

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
                TopicToCreate(TopicId(1), TopicName("very-first-topic"), GroupId(234))
            )

            assertThat(result.id, equalTo(TopicId(1)))
            assertThat(result.name, equalTo(TopicName("very-first-topic")))
            assertThat(result.groupId, equalTo(GroupId(234)))
            assertThat(testInstance.count(TopicQuery(groupIds = listOf())), equalTo(1UL))
        }

        @Test
        fun `Bug - able to create realistic topic name`() {
            testInstance.create(
                CmdId(1),
                TopicToCreate(TopicId(1), TopicName("very-first-topic"), GroupId(345))
            )

            val result = testInstance.create(
                CmdId(2),
                TopicToCreate(TopicId(2), TopicName("func::created"), GroupId(345))
            )
            assertThat(result.id, equalTo(TopicId(2)))
            assertThat(result.name, equalTo(TopicName("func::created")))

            assertThat(testInstance.count(TopicQuery(groupIds = listOf())), equalTo(2UL))
        }

        @Test
        fun `Does not creat a new entry if topic already exists`() {
            testInstance.create(
                CmdId(1),
                TopicToCreate(TopicId(1), TopicName("very-first-topic"), GroupId(1))
            )

            val throwable = assertThrows<IllegalArgumentException> {
                testInstance.create(
                    CmdId(2),
                    TopicToCreate(TopicId(2), TopicName("very-first-topic"), GroupId(1))
                )
            }
            assertThat(throwable.message, equalTo("Topic already exists"))

            assertThat(testInstance.count(TopicQuery(groupIds = listOf())), equalTo(1UL))
        }

        private val testInstance = MemoryBrokerTopicsRepository()
    }

}