package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.*
import io.hamal.repository.sqlite.log.*
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        BrokerConsumersRepository::class -> SqliteBrokerConsumersRepository(
            SqliteBrokerConsumers(createTempDirectory("sqlite_broker_consumers_test"))
        ) as REPO

        BrokerRepository::class -> SqliteBrokerRepository(
            SqliteBroker(createTempDirectory("sqlite_broker_test"))
        ) as REPO

        BrokerTopicsRepository::class -> SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        SegmentRepository::class ->
            SqliteSegmentRepository(
                SqliteSegment(
                    Segment.Id(2810),
                    TopicId(1506),
                    createTempDirectory("sqlite_topic_test")
                )
            ) as REPO

        TopicRepository::class -> SqliteTopicRepository(
            Topic(TopicId(23), GroupId(1), TopicName("test-topic")),
            createTempDirectory("sqlite_topic_test")
        ) as REPO

        else -> TODO()
    }
}