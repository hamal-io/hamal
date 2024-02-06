package io.hamal.repository.fixture

import io.hamal.repository.api.log.LogBrokerRepository
import io.hamal.repository.sqlite.new_log.LogBrokerSqliteRepository
import kotlin.io.path.createTempDirectory
import kotlin.reflect.KClass

object SqliteFixture : BaseTestFixture {

    @Suppress("UNCHECKED_CAST")
    override fun <REPO : Any> provideImplementation(interfaceClass: KClass<out REPO>): REPO = when (interfaceClass) {
        LogBrokerRepository::class -> LogBrokerSqliteRepository(createTempDirectory("sqlite_log_broker_test")) as REPO
        else -> TODO()
    }
}