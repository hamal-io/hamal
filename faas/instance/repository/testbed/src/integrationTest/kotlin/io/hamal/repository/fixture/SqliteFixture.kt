package io.hamal.repository.fixture

import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.sqlite.log.SqliteBrokerTopics
import io.hamal.repository.sqlite.log.SqliteLogBroker
import io.hamal.repository.sqlite.log.SqliteLogBrokerRepository
import io.hamal.repository.sqlite.log.SqliteBrokerTopicsRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {

        LogBrokerRepository::class ->  SqliteLogBrokerRepository(
            SqliteLogBroker(createTempDirectory("sqlite_broker_test"))
        ) as REPO

        BrokerTopicsRepository::class -> SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        else -> TODO()
    }
}