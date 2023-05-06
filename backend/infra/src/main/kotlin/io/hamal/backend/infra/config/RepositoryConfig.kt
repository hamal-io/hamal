package io.hamal.backend.infra.config

import io.hamal.backend.repository.api.JobDefinitionRepository
import io.hamal.backend.repository.api.log.Broker
import io.hamal.backend.repository.api.log.BrokerRepository
import io.hamal.backend.repository.sqlite.domain.DefaultJobDefinitionRepository
import io.hamal.backend.repository.sqlite.domain.DefaultJobDefinitionRepository.Config
import io.hamal.backend.repository.sqlite.log.DefaultBrokerRepository
import io.hamal.lib.Shard
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
open class RepositoryConfig {

    @Bean
    open fun brokerRepository(): BrokerRepository {
        return DefaultBrokerRepository(Broker(Broker.Id(1), Path("/tmp/hamal")))
    }

    @Bean
    open fun jobDefinitionRepository(): JobDefinitionRepository = DefaultJobDefinitionRepository(
        Config(
            Path("/tmp/hamal"),
            Shard(0)
        )
    )
}