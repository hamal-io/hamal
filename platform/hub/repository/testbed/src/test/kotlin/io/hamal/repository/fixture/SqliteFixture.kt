package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.api.log.Topic
import io.hamal.repository.api.log.TopicRepository
import io.hamal.repository.sqlite.log.SqliteBrokerTopics
import io.hamal.repository.sqlite.log.SqliteBrokerTopicsRepository
import io.hamal.repository.sqlite.log.SqliteTopicRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {

        BrokerTopicsRepository::class -> SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        TopicRepository::class -> SqliteTopicRepository(
            Topic(TopicId(23), GroupId(1), TopicName("test-topic")),
            createTempDirectory("sqlite_topic_test")
        ) as REPO

        else -> TODO()
    }
}