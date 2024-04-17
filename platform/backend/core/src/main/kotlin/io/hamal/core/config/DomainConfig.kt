package io.hamal.core.config

import io.hamal.core.component.GenerateCmdIdImpl
import io.hamal.core.component.GenerateDomainIdImpl
import io.hamal.lib.common.snowflake.PartitionSourceImpl
import io.hamal.lib.common.snowflake.SnowflakeGenerator
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.GenerateDomainId
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainConfig(
    @Value("\${io.hamal.instance:1}") val instance: Int
) {

    @Bean
    open fun generateDomainId(): GenerateDomainId = GenerateDomainIdImpl(generator)

    @Bean
    open fun generateCmdId(): GenerateCmdId = GenerateCmdIdImpl(generator)

    private val generator = SnowflakeGenerator(PartitionSourceImpl(instance))
}
