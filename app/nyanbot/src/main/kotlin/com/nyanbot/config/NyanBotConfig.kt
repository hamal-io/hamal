package com.nyanbot.config

import io.hamal.lib.domain.CmdIdGeneratorImpl
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.IdGeneratorImpl
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
    open fun generateDomainId(): GenerateDomainId = IdGeneratorImpl

    @Bean
    open fun generateCmdId(): GenerateCmdId = CmdIdGeneratorImpl

}