package io.hamal.faas.instance.backend.repository.log

import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.backend.repository.api.log.BrokerTopicsRepository.TopicToCreate
import io.hamal.faas.instance.backend.repository.AbstractUnitTest
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.TestFactory

internal class BrokerTopicsRepositoryTest : AbstractUnitTest() {

    @TestFactory
    fun `Topic not found by id`() = runWith(BrokerTopicsRepository::class) { testInstance ->
        testInstance.setupTopic()

        val result = testInstance.find(TopicId(1234))
        assertThat(result, nullValue())
    }

    @TestFactory
    fun `Topic found by id`() = runWith(BrokerTopicsRepository::class) { testInstance ->
        testInstance.setupTopic()

        with(testInstance.find(TopicId(5432))!!) {
            assertThat(id, equalTo(TopicId(5432)))
            assertThat(name, equalTo(TopicName("created-topic")))
        }
    }

    @TestFactory
    fun `Topic not found by name`() = runWith(BrokerTopicsRepository::class) { testInstance ->
        testInstance.setupTopic()

        val result = testInstance.find(TopicName("this-topic-does-not-exist"))
        assertThat(result, nullValue())
    }

    @TestFactory
    fun `Topic found by name`() = runWith(BrokerTopicsRepository::class) { testInstance ->
        testInstance.setupTopic()

        with(testInstance.find(TopicName("created-topic"))!!) {
            assertThat(id, equalTo(TopicId(5432)))
            assertThat(name, equalTo(TopicName("created-topic")))
        }
    }

    private fun BrokerTopicsRepository.setupTopic() {
        create(
            CmdId(1), TopicToCreate(
                id = TopicId(5432),
                name = TopicName("created-topic")
            )
        )
    }
}
