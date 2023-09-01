package io.hamal.repository.fixture

import io.hamal.repository.api.log.BrokerTopicsRepository
import io.hamal.repository.sqlite.log.SqliteBrokerTopics
import io.hamal.repository.sqlite.log.SqliteBrokerTopicsRepository
import java.nio.file.Files.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {

        BrokerTopicsRepository::class -> SqliteBrokerTopicsRepository(
            SqliteBrokerTopics(createTempDirectory("sqlite_broker_topics_test"))
        ) as REPO

        else -> TODO()
    }
}