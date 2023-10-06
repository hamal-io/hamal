package io.hamal.core.config

import io.hamal.core.component.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SecurityConfig {
    @Bean
    open fun generateSalt(): GenerateSalt = SecureRandomSalt

    @Bean
    open fun encodePassword(): EncodePassword = PBKDF2

    @Bean
    open fun generateToken(): GenerateToken = DomainGenerateToken

}