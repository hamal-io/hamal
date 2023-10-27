package io.hamal.repository.log

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicQuery
import io.hamal.repository.api.log.BrokerTopicsRepository.TopicToCreate
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class BrokerTopicsRepositoryTest : AbstractUnitTest() {

    @TestFactory
    fun `Creates a new topic`() = runWith(BrokerTopicsRepository::class) {
        val result = create(
            CmdId(1),
            TopicToCreate(TopicId(1), TopicName("very-first-topic"), NamespaceId(23), GroupId(234))
        )

        assertThat(result.id, equalTo(TopicId(1)))
        assertThat(result.name, equalTo(TopicName("very-first-topic")))
        assertThat(result.namespaceId, equalTo(NamespaceId(23)))
        assertThat(result.groupId, equalTo(GroupId(234)))
        assertThat(count(TopicQuery(groupIds = listOf())), equalTo(1UL))
    }

    @TestFactory
    fun `Bug - able to create realistic topic name`() = runWith(BrokerTopicsRepository::class) {
        create(
            CmdId(1),
            TopicToCreate(TopicId(1), TopicName("very-first-topic"), NamespaceId(23), GroupId(345))
        )

        val result = create(
            CmdId(2),
            TopicToCreate(TopicId(2), TopicName("func::created"), NamespaceId(23), GroupId(345))
        )
        assertThat(result.id, equalTo(TopicId(2)))
        assertThat(result.name, equalTo(TopicName("func::created")))

        assertThat(count(TopicQuery(groupIds = listOf())), equalTo(2UL))
    }

    @TestFactory
    fun `Does not create a new entry if topic alread exists in namespace`() =
        runWith(BrokerTopicsRepository::class) {
            create(
                CmdId(1),
                TopicToCreate(TopicId(1), TopicName("very-first-topic"), NamespaceId(23), GroupId(1))
            )

            val throwable = assertThrows<IllegalArgumentException> {
                create(
                    CmdId(2),
                    TopicToCreate(TopicId(2), TopicName("very-first-topic"), NamespaceId(23), GroupId(1))
                )
            }
            assertThat(throwable.message, equalTo("Topic already exists"))
            assertThat(count(TopicQuery(groupIds = listOf())), equalTo(1UL))
        }

    @TestFactory
    fun `Create a new topic even topic name exists for different namespaces`() =
        runWith(BrokerTopicsRepository::class) {
            create(
                CmdId(1),
                TopicToCreate(TopicId(1), TopicName("very-first-topic"), NamespaceId(22), GroupId(1))
            )

            create(
                CmdId(2),
                TopicToCreate(TopicId(2), TopicName("very-first-topic"), NamespaceId(33), GroupId(1))
            )

            create(
                CmdId(3),
                TopicToCreate(TopicId(3), TopicName("very-first-topic"), NamespaceId(44), GroupId(1))
            )
        }


    @TestFactory
    fun `Topic name only exists in different namespace`() = runWith(BrokerTopicsRepository::class) {
        setupTopic()

        create(
            CmdId(123), TopicToCreate(
                id = TopicId(2345),
                name = TopicName("created-topic"),
                namespaceId = NamespaceId(10000),
                groupId = GroupId(1)
            )
        )
    }


    @TestFactory
    fun `Topic name already exists in namespace`() = runWith(BrokerTopicsRepository::class) {
        setupTopic()

        val exception = assertThrows<IllegalArgumentException> {
            create(
                CmdId(123), TopicToCreate(
                    id = TopicId(2345),
                    name = TopicName("created-topic"),
                    namespaceId = NamespaceId(23),
                    groupId = GroupId(1)
                )
            )
        }

        assertThat(exception.message, equalTo("Topic already exists"))
    }

    @TestFactory
    fun `Topic not found by id`() = runWith(BrokerTopicsRepository::class) {
        setupTopic()

        val result = find(TopicId(1234))
        assertThat(result, nullValue())
    }

    @TestFactory
    fun `Topic found by id`() = runWith(BrokerTopicsRepository::class) {
        setupTopic()

        with(find(TopicId(5432))!!) {
            assertThat(id, equalTo(TopicId(5432)))
            assertThat(name, equalTo(TopicName("created-topic")))
        }
    }

    @TestFactory
    fun `Topic not found by name`() = runWith(BrokerTopicsRepository::class) {
        setupTopic()

        val result = find(NamespaceId(23), TopicName("this-topic-does-not-exist"))
        assertThat(result, nullValue())
    }

    @TestFactory
    fun `Topic not found by name - exist in different namespace only`() =
        runWith(BrokerTopicsRepository::class) {
            setupTopic()

            val result = find(NamespaceId(22223333), TopicName("created-topic"))
            assertThat(result, nullValue())
        }

    @TestFactory
    fun `Topic found by name`() = runWith(BrokerTopicsRepository::class) {
        setupTopic()

        with(find(NamespaceId(23), TopicName("created-topic"))!!) {
            assertThat(id, equalTo(TopicId(5432)))
            assertThat(name, equalTo(TopicName("created-topic")))
        }
    }

    @TestFactory
    fun `List and count on empty repository`() = runWith(BrokerTopicsRepository::class) {
        val query = TopicQuery(groupIds = listOf())

        val resultList = list(query)
        assertThat(resultList, equalTo(listOf()))

        val resultCount = count(query)
        assertThat(resultCount, equalTo(0UL))
    }

    @TestFactory
    fun `List and count all topics`() = runWith(BrokerTopicsRepository::class) {
        setupTopics()
        val query = TopicQuery(groupIds = listOf(), limit = Limit(10))

        val resultList = list(query)
        assertThat(resultList, hasSize(9))

        assertThat(resultList[0].name, equalTo(TopicName("topic-nine")))
        assertThat(resultList[4].name, equalTo(TopicName("topic-five")))
        assertThat(resultList[8].name, equalTo(TopicName("topic-one")))

        val resultCount = count(query)
        assertThat(resultCount, equalTo(9UL))
    }

    @TestFactory
    fun `List and count all group topics`() = runWith(BrokerTopicsRepository::class) {
        setupTopics()
        val query = TopicQuery(groupIds = listOf(GroupId(2)), limit = Limit(10))

        val resultList = list(query)
        assertThat(resultList, hasSize(2))

        assertThat(resultList[0].name, equalTo(TopicName("topic-eight")))
        assertThat(resultList[1].name, equalTo(TopicName("topic-seven")))

        val resultCount = count(query)
        assertThat(resultCount, equalTo(2UL))
    }

    @TestFactory
    fun `Limit list and count`() = runWith(BrokerTopicsRepository::class) {
        setupTopics()
        val query = TopicQuery(groupIds = listOf(), limit = Limit(5))

        val resultList = list(query)
        assertThat(resultList, hasSize(5))

        assertThat(resultList[0].name, equalTo(TopicName("topic-nine")))
        assertThat(resultList[4].name, equalTo(TopicName("topic-five")))

        val resultCount = count(query)
        assertThat(resultCount, equalTo(9UL))
    }

    @TestFactory
    fun `Skip and limit - list and count`() = runWith(BrokerTopicsRepository::class) {
        setupTopics()
        val query = TopicQuery(groupIds = listOf(), limit = Limit(1), afterId = TopicId(5))

        val resultList = list(query)
        assertThat(resultList, hasSize(1))

        assertThat(resultList[0].name, equalTo(TopicName("topic-four")))

        val resultCount = count(query)
        assertThat(resultCount, equalTo(4UL))
    }

    @TestFactory
    fun `List and count by providing topic names`() = runWith(BrokerTopicsRepository::class) {
        setupTopics()

        val query = TopicQuery(
            groupIds = listOf(),
            limit = Limit(10),
            names = listOf(
                TopicName("topic-five"),
                TopicName("topic-eight"),
                TopicName("topic-ten")
            )
        )

        val resultList = list(query)
        assertThat(resultList, hasSize(2))

        assertThat(resultList[0].name, equalTo(TopicName("topic-eight")))
        assertThat(resultList[1].name, equalTo(TopicName("topic-five")))

        val resultCount = count(query.apply { limit = Limit(1) })
        assertThat(resultCount, equalTo(2UL))
    }

    private fun BrokerTopicsRepository.setupTopic() {
        create(
            CmdId(1), TopicToCreate(
                id = TopicId(5432),
                name = TopicName("created-topic"),
                namespaceId = NamespaceId(23),
                groupId = GroupId(1)
            )
        )
    }

    private fun BrokerTopicsRepository.setupTopics() {
        create(CmdId(1), TopicToCreate(TopicId(1), TopicName("topic-one"), NamespaceId(1), GroupId(1)))
        create(CmdId(2), TopicToCreate(TopicId(2), TopicName("topic-two"), NamespaceId(1), GroupId(1)))
        create(CmdId(3), TopicToCreate(TopicId(3), TopicName("topic-three"), NamespaceId(1), GroupId(1)))
        create(CmdId(4), TopicToCreate(TopicId(4), TopicName("topic-four"), NamespaceId(1), GroupId(1)))
        create(CmdId(5), TopicToCreate(TopicId(5), TopicName("topic-five"), NamespaceId(1), GroupId(1)))
        create(CmdId(6), TopicToCreate(TopicId(6), TopicName("topic-six"), NamespaceId(1), GroupId(1)))
        create(CmdId(7), TopicToCreate(TopicId(7), TopicName("topic-seven"), NamespaceId(2), GroupId(2)))
        create(CmdId(8), TopicToCreate(TopicId(8), TopicName("topic-eight"), NamespaceId(2), GroupId(2)))
        create(CmdId(9), TopicToCreate(TopicId(9), TopicName("topic-nine"), NamespaceId(3), GroupId(3)))
    }
}
