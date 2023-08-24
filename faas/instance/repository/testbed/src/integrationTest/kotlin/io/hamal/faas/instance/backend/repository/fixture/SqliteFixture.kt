package io.hamal.faas.instance.backend.repository.fixture

import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.backend.repository.api.log.BrokerTopicsRepository
import io.hamal.backend.repository.sqlite.log.SqliteBrokerTopics
import io.hamal.backend.repository.sqlite.log.SqliteLogBroker
import io.hamal.backend.repository.sqlite.log.SqliteLogBrokerRepository
import io.hamal.backend.repository.sqlite.log.SqliteBrokerTopicsRepository
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