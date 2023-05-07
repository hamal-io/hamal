package io.hamal.backend.infra.config

import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.JobRepository
import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.memory.domain.MemoryJobDefinitionRepository
import io.hamal.backend.repository.memory.domain.MemoryJobRepository
import io.hamal.backend.repository.sqlite.log.DefaultBrokerRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
open class RepositoryConfig {
    @Bean
    open fun brokerRepository(): BrokerRepository {
        return DefaultBrokerRepository(Broker(Broker.Id(1), Path("/tmp/hamal")))
    }

    //    @Bean
//    open fun jobDefinitionRepository(): JobDefinitionRepository = SqliteJobDefinitionRepository(
//        Config(
//            Path("/tmp/hamal"),
//            Shard(0)
//        )
//    )
    @Bean
    open fun jobDefinitionRepository(): JobDefinitionRepository = MemoryJobDefinitionRepository

    @Bean
    open fun jobRepository() : JobRepository = MemoryJobRepository
}