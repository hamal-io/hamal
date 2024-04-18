package com.nyanbot.config

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.GenerateDomainId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

data class NyanBotBasePath(val value: String)

@Configuration
@Profile("!test")
class NyanBotConfig {

    @Bean
    fun basePath() = NyanBotBasePath("/opt/nyanbot")

    @Bean
    open fun generateDomainId(): GenerateDomainId = GenerateDomainIdImpl(SnowflakeGenerator(PartitionSourceImpl(1)))

}

class GenerateDomainIdImpl(private val generator: SnowflakeGenerator) : GenerateDomainId {
    override fun <ID : ValueObjectId> invoke(ctor: (SnowflakeId) -> ID): ID {
        return ctor(generator.next())
    }
}


