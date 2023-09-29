package io.hamal.repository.fixture

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.repository.api.*
import io.hamal.repository.api.log.*
import io.hamal.repository.sqlite.SqliteAuthRepository
import io.hamal.repository.sqlite.SqliteStateRepository
import io.hamal.repository.sqlite.log.*
import io.hamal.repository.sqlite.record.account.SqliteAccountRepository
import io.hamal.repository.sqlite.record.exec.SqliteExecRepository
import io.hamal.repository.sqlite.record.func.SqliteFuncRepository
import io.hamal.repository.sqlite.record.namespace.SqliteNamespaceRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        AccountRepository::class -> SqliteAccountRepository(
            SqliteAccountRepository.Config(createTempDirectory("sqlite_account_test"))
        ) as REPO

        AuthRepository::class -> SqliteAuthRepository(
            SqliteAuthRepository.Config(createTempDirectory("sqlite_auth_test"))
        ) as REPO

        BrokerConsumersRepository::class -> SqliteBrokerConsumersRepository(
            SqliteBrokerConsumers(createTempDirectory("sqlite_broker_consumers_test"))
        ) as REPO

        BrokerRepository::class -> SqliteBrokerRepository(
            SqliteBroker(createTempDirectory("sqlite_broker_test"))
        ) as REPO

        BrokerTopicsRepository::class -> SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        ExecRepository::class -> SqliteExecRepository(
            SqliteExecRepository.Config(createTempDirectory("sqlite_exec_test"))
        ) as REPO

        FuncRepository::class -> SqliteFuncRepository(
            SqliteFuncRepository.Config(createTempDirectory("sqlite_func_test"))
        ) as REPO

        NamespaceRepository::class -> SqliteNamespaceRepository(
            SqliteNamespaceRepository.Config(createTempDirectory("sqlite_namespace_test"))
        ) as REPO

        SegmentRepository::class ->
            SqliteSegmentRepository(
                SqliteSegment(
                    Segment.Id(2810),
                    TopicId(1506),
                    createTempDirectory("sqlite_topic_test")
                )
            ) as REPO

        StateRepository::class ->
            SqliteStateRepository(
                SqliteStateRepository.Config(createTempDirectory("sqlite_state_test"))
            ) as REPO

        TopicRepository::class -> SqliteTopicRepository(
            Topic(TopicId(23), GroupId(1), TopicName("test-topic")),
            createTempDirectory("sqlite_topic_test")
        ) as REPO

        else -> TODO()
    }
}