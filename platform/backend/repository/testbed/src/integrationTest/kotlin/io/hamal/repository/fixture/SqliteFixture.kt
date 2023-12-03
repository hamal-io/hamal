package io.hamal.repository.fixture

import io.hamal.repository.api.log.BrokerRepository
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.sqlite.log.BrokerTopicsSqlite
import io.hamal.repository.sqlite.log.BrokerSqlite
import io.hamal.repository.sqlite.log.BrokerSqliteRepository
import io.hamal.repository.sqlite.log.BrokerTopicsSqliteRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {

        BrokerRepository::class ->  BrokerSqliteRepository(
            BrokerSqlite(createTempDirectory("sqlite_broker_test"))
        ) as REPO

        BrokerTopicsRepository::class -> BrokerTopicsSqliteRepository(
            BrokerTopicsSqlite(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        else -> TODO()
    }
}